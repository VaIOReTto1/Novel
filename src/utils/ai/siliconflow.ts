import {
    DEFAULT_SYSTEM_PROMPT,
    OpenAIStyleMessage,
    SILICONFLOW_API_BASE,
    SILICONFLOW_API_KEY,
    StreamCallbacks,
} from '../../config/ai';

export type StreamParams = {
    messages: OpenAIStyleMessage[];
    deepThinkEnabled: boolean;
    abortSignal?: AbortSignal;
} & StreamCallbacks;

function isReactNative(): boolean {
    try {
        // RN polyfills navigator with product === 'ReactNative'
        return typeof navigator !== 'undefined' && (navigator as unknown as { product?: string }).product === 'ReactNative';
    } catch {
        return false;
    }
}

function buildPayload(
    messages: OpenAIStyleMessage[],
    deepThinkEnabled: boolean,
) {
    // DeepSeek 官方模型名：deepseek-reasoner（R1），deepseek-chat（V3）
    const model = deepThinkEnabled ? 'deepseek-reasoner' : 'deepseek-chat';
    const sysExists = messages.some(m => m.role === 'system');
    const finalMessages = sysExists
        ? messages
        : [
            { role: 'system', content: DEFAULT_SYSTEM_PROMPT } as OpenAIStyleMessage,
            ...messages,
        ];

    return {
        url: SILICONFLOW_API_BASE,
        body: {
            model,
            messages: finalMessages,
            stream: true,
        },
    } as const;
}

function parseAndDispatchSSEChunk(
    chunkText: string,
    callbacks: StreamCallbacks,
) {
    const lines = chunkText
        .split('\n')
        .map(l => l.trim())
        .filter(Boolean);

    for (const line of lines) {
        if (!line.startsWith('data:')) {
            continue;
        }
        const data = line.slice(5).trim();
        if (data === '[DONE]') {
            callbacks.onDone?.();
            continue;
        }
        try {
            const json = JSON.parse(data);
            const delta = json.choices?.[0]?.delta ?? {};
            // SiliconFlow: reasoning_content 与 content 区分推理与回复内容
            const content: string | undefined = delta.content ?? null;
            const reasoning: string | undefined = delta.reasoning_content ?? null;
            if (typeof content === 'string' && content.length) {
                callbacks.onContentDelta?.(content);
            }
            if (typeof reasoning === 'string' && reasoning.length) {
                callbacks.onReasoningDelta?.(reasoning);
            }
            if (json.usage) {
                callbacks.onUsage?.(json.usage);
            }
        } catch {
            // ignore malformed json lines
        }
    }
}

/**
 * React Native XHR streaming fallback.
 * Uses incremental onprogress with responseText to dispatch SSE chunks ASAP.
 */
async function streamViaXHR(
    url: string,
    body: unknown,
    headers: Record<string, string>,
    callbacks: StreamCallbacks,
    abortSignal?: AbortSignal,
): Promise<void> {
    return new Promise<void>((resolve, reject) => {
        const XHR = (globalThis as unknown as { XMLHttpRequest?: typeof XMLHttpRequest }).XMLHttpRequest ?? (typeof XMLHttpRequest !== 'undefined' ? XMLHttpRequest : undefined);
        if (!XHR) {
            reject(new Error('XMLHttpRequest is not available'));
            return;
        }

        const xhr = new XHR();
        let doneCalled = false;
        const safeDone = () => {
            if (doneCalled) {
                return;
            }
            doneCalled = true;
            callbacks.onDone?.();
        };

        let lastIndex = 0;
        let buffer = '';

        xhr.open('POST', url, true);
        // set headers
        try {
            for (const [k, v] of Object.entries(headers)) {
                xhr.setRequestHeader(k, v);
            }
        } catch { }

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 2) {
                try {
                    const ct = xhr.getResponseHeader('content-type') || '';
                    console.log('[SiliconFlow][XHR] headers', { contentType: ct });
                } catch { }
            }
        };

        xhr.onprogress = function () {
            try {
                const text: string = xhr.responseText || '';
                if (text.length <= lastIndex) {
                    return;
                }
                const incoming = text.slice(lastIndex);
                lastIndex = text.length;
                buffer += incoming;
                // Normalize CRLF to LF to simplify splitting
                buffer = buffer.replace(/\r\n/g, '\n');
                let idx = buffer.indexOf('\n\n');
                while (idx !== -1) {
                    const chunk = buffer.slice(0, idx);
                    parseAndDispatchSSEChunk(chunk, {
                        ...callbacks,
                        onDone: safeDone,
                    });
                    buffer = buffer.slice(idx + 2);
                    idx = buffer.indexOf('\n\n');
                }
            } catch (e) {
                console.warn('[SiliconFlow][XHR] onprogress error', e);
            }
        };

        xhr.onerror = function () {
            const err = new Error('SiliconFlow XHR network error');
            callbacks.onError?.(err);
            reject(err);
        };

        xhr.onload = function () {
            try {
                // Flush remaining buffer if any
                if (buffer.trim().length > 0) {
                    parseAndDispatchSSEChunk(buffer, {
                        ...callbacks,
                        onDone: safeDone,
                    });
                    buffer = '';
                }
                safeDone();
                resolve();
            } catch (e) {
                reject(e as Error);
            }
        };

        if (abortSignal) {
            const onAbort = () => {
                try {
                    xhr.abort();
                } catch { }
                reject(new Error('Aborted'));
            };
            if ((abortSignal as unknown as { aborted?: boolean }).aborted) {
                onAbort();
                return;
            }
            abortSignal.addEventListener('abort', onAbort, { once: true });
        }

        try {
            xhr.send(JSON.stringify(body));
        } catch (e) {
            reject(e as Error);
        }
    });
}

export async function streamChatCompletion(
    params: StreamParams,
): Promise<void> {
    const {
        messages,
        deepThinkEnabled,
        onContentDelta,
        onReasoningDelta,
        onUsage,
        onDone,
        onError,
        abortSignal,
    } = params;
    const { url, body } = buildPayload(messages, deepThinkEnabled);

    try {
        console.log('[SiliconFlow] request', {
            model: (body as any).model,
            len: messages.length,
            url,
        });
        const headers = {
            Authorization: `Bearer ${SILICONFLOW_API_KEY}`,
            'Content-Type': 'application/json',
            // 优先 SSE，其次 JSON 直出
            Accept: 'text/event-stream, application/json',
        } as const;

        // Prefer XHR in React Native to achieve true incremental streaming
        if (isReactNative()) {
            try {
                await streamViaXHR(
                    url,
                    body,
                    headers as Record<string, string>,
                    { onContentDelta, onReasoningDelta, onUsage, onDone, onError },
                    abortSignal,
                );
                return;
            } catch (e) {
                console.warn('[SiliconFlow] XHR stream failed, fallback to fetch', e);
            }
        }

        const res = await fetch(url, {
            method: 'POST',
            headers,
            body: JSON.stringify(body),
            signal: abortSignal,
        });

        if (!res.ok) {
            const text = await res.text().catch(() => '');
            throw new Error(
                `SiliconFlow error: ${res.status} ${res.statusText} ${text}`,
            );
        }

        const contentType =
            (res.headers && (res.headers as any).get?.('content-type')) || '';
        console.log('[SiliconFlow] response headers', { contentType });

        const reader: ReadableStreamDefaultReader<Uint8Array> | undefined = (
            res as any
        ).body?.getReader?.();

        if (!reader || contentType.includes('application/json')) {
            console.log('[SiliconFlow] response not stream, fallback to non-stream');
            const textBody = await res.text();
            // 1) 优先解析标准JSON（非流直出）
            try {
                const json = JSON.parse(textBody);
                const message = json?.choices?.[0]?.message ?? {};
                const txt: string = message?.content ?? '';
                const rtxt: string = message?.reasoning_content ?? '';
                if (txt) {
                    onContentDelta?.(txt);
                }
                if (rtxt) {
                    onReasoningDelta?.(rtxt);
                }
                if (json?.usage) {
                    onUsage?.(json.usage);
                }
                onDone?.();
                return;
            } catch { }

            // 2) 文本是 SSE 片段（如某些运行时无法暴露 reader）；逐段解析 data: 行
            if (textBody.includes('data:')) {
                let doneCalled = false;
                const safeDone = () => {
                    if (doneCalled) {
                        return;
                    }
                    doneCalled = true;
                    onDone?.();
                };
                const chunks = textBody.replace(/\r\n/g, '\n').split('\n\n');
                for (const evt of chunks) {
                    if (!evt.trim()) {
                        continue;
                    }
                    parseAndDispatchSSEChunk(evt, {
                        onContentDelta,
                        onReasoningDelta,
                        onUsage,
                        onDone: safeDone,
                        onError,
                    });
                }
                safeDone();
                return;
            }

            // 3) 兜底：当作纯文本
            if (textBody) {
                onContentDelta?.(textBody);
            }
            onDone?.();
            return;
        }

        // 参考文章中“即时 SSE 通信”的实现：边读边消费，按双换行切块
        const decoder = new TextDecoder('utf-8');
        let buffer = '';
        let doneCalled = false;
        const safeDone = () => {
            if (doneCalled) {
                return;
            }
            doneCalled = true;
            onDone?.();
        };
        console.log('[SiliconFlow] start reading stream');
        // Iterative loop instead of recursion
        for (;;) {
            const { value, done } = await reader.read();
            if (done) {
                if (buffer.trim().length) {
                    parseAndDispatchSSEChunk(buffer, {
                        onContentDelta,
                        onReasoningDelta,
                        onUsage,
                        onDone: safeDone,
                        onError,
                    });
                }
                console.log('[SiliconFlow] stream done');
                safeDone();
                break;
            }
            buffer += decoder.decode(value, { stream: true });
            // Normalize CRLF to LF before splitting
            buffer = buffer.replace(/\r\n/g, '\n');
            let idx = buffer.indexOf('\n\n');
            while (idx !== -1) {
                const chunk = buffer.slice(0, idx);
                parseAndDispatchSSEChunk(chunk, {
                    onContentDelta,
                    onReasoningDelta,
                    onUsage,
                    onDone: safeDone,
                    onError,
                });
                buffer = buffer.slice(idx + 2);
                idx = buffer.indexOf('\n\n');
            }
        }
    } catch (e: any) {
        console.warn('[SiliconFlow] stream error', e);
        onError?.(e instanceof Error ? e : new Error(String(e)));
    }
}
