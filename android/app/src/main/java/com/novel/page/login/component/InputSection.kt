package com.novel.page.login.component

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.novel.page.component.ImageLoadingStrategy
import com.novel.page.component.NovelImageView
import com.novel.page.component.NovelText
import com.novel.page.component.NovelTextField
import com.novel.page.login.viewmodel.CaptchaState
import com.novel.page.login.viewmodel.LoginForm
import com.novel.page.login.viewmodel.RegisterForm
import com.novel.page.login.viewmodel.ValidationResults
import com.novel.ui.theme.NovelColors
import com.novel.utils.debounceClickable
import com.novel.utils.ssp
import com.novel.utils.wdp

// 动画常量
private object AnimationConfig {
    const val DURATION_MS = 600
    const val DELAY_MS = 200
}

/**
 * 输入区域，包含用户名、密码、确认密码和验证码 - MVI版本
 */
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun InputSection(
    isLoginMode: Boolean,
    loginForm: LoginForm,
    registerForm: RegisterForm,
    validationResults: ValidationResults,
    captchaState: CaptchaState,
    onPhoneInput: (String) -> Unit,
    onPasswordInput: (String) -> Unit,
    onPasswordConfirmInput: (String) -> Unit,
    onVerifyCodeInput: (String) -> Unit,
    onRefreshCaptcha: () -> Unit
) {
    // 构建注册模式切换的 Transition
    val transition =
        updateTransition(targetState = !isLoginMode, label = "registerTransition")

    // 弹性位移动画：从 -40.wdp 到 0.wdp，下滑时带弹性回弹
    val sectionOffset by transition.animateDp(
        transitionSpec = {
            spring(
                stiffness = Spring.StiffnessLow,              // 更柔软的弹簧
                dampingRatio = Spring.DampingRatioMediumBouncy // 适度的回弹
            )
        },
        label = "sectionOffset"
    ) { state ->
        if (state) 0.wdp else (-40).wdp
    }

    // 透明度动画：用 tween 控制总时长，并加一点延迟
    val sectionAlpha by transition.animateFloat(
        transitionSpec = {
            tween(
                durationMillis = 800,
                delayMillis = 200,
                easing = FastOutSlowInEasing
            )
        },
        label = "sectionAlpha"
    ) { state ->
        if (state) 1f else 0f
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.5.wdp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 手机号输入区域
        NovelTextField(
            value = if (isLoginMode) loginForm.phone else registerForm.phone,
            onValueChange = onPhoneInput,
            modifier = Modifier
                .height(45.wdp)
                .width(329.wdp),
            placeText = "请输入手机号",
            isError = validationResults.phoneError != null,
            errorMessage = null
        )
        
        // 手机号错误信息区域 - 使用Box避免弹跳效果
        Box(
            modifier = Modifier.height(19.wdp).fillMaxWidth()
        ) {
            // 手机号错误信息 - 显示在输入框下方
            if (validationResults.phoneError != null) {
                NovelText(
                    text = validationResults.phoneError,
                    color = NovelColors.NovelError,
                    fontSize = 10.ssp,
                    lineHeight = 10.ssp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.wdp)
                )
            }
        }

        // 密码输入区域
        NovelTextField(
            value = if (isLoginMode) loginForm.password else registerForm.password,
            onValueChange = onPasswordInput,
            modifier = Modifier
                .height(45.wdp)
                .width(329.wdp),
            placeText = "请输入密码",
            isPassword = true,
            isError = validationResults.passwordError != null,
            errorMessage = null
        )
        
        // 密码错误信息区域 - 使用Box避免弹跳效果
        Box(
            modifier = Modifier.height(19.wdp).fillMaxWidth()
        ) {
            // 密码错误信息 - 显示在输入框下方
            if (validationResults.passwordError != null) {
                NovelText(
                    text = validationResults.passwordError,
                    color = NovelColors.NovelError,
                    fontSize = 10.ssp,
                    lineHeight = 10.ssp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 8.wdp)
                )
            }
        }

        // 注册模式的额外字段
        if (!isLoginMode)
            AnimatedVisibility(
                visible = true,
                enter = expandVertically(
                    animationSpec = tween(
                        AnimationConfig.DURATION_MS,
                        AnimationConfig.DELAY_MS,
                        FastOutSlowInEasing
                    )
                ) +
                        fadeIn(
                            animationSpec = tween(
                                AnimationConfig.DURATION_MS,
                                AnimationConfig.DELAY_MS,
                                FastOutSlowInEasing
                            )
                        ),
                exit = shrinkVertically(
                    animationSpec = tween(
                        AnimationConfig.DURATION_MS,
                        easing = FastOutSlowInEasing
                    )
                ) +
                        fadeOut(
                            animationSpec = tween(
                                AnimationConfig.DURATION_MS,
                                easing = FastOutSlowInEasing
                            )
                        )
            ) {
                Column(
                    modifier = Modifier
                        .offset(y = sectionOffset)
                        .alpha(sectionAlpha),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // 确认密码输入区域
                    NovelTextField(
                        value = registerForm.passwordConfirm,
                        onValueChange = onPasswordConfirmInput,
                        modifier = Modifier
                            .height(45.wdp)
                            .width(329.wdp),
                        placeText = "再次输入密码",
                        isPassword = true,
                        isError = validationResults.passwordConfirmError != null,
                        errorMessage = null
                    )
                    
                    // 确认密码错误信息区域 - 使用Box避免弹跳效果
                    Box(
                        modifier = Modifier.height(19.wdp).fillMaxWidth()
                    ) {
                        // 确认密码错误信息 - 显示在输入框下方
                        if (validationResults.passwordConfirmError != null) {
                            NovelText(
                                text = validationResults.passwordConfirmError,
                                color = NovelColors.NovelError,
                                fontSize = 10.ssp,
                                lineHeight = 10.ssp,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 8.wdp)
                            )
                        }
                    }

                    // 验证码输入区域
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        NovelTextField(
                            value = registerForm.verifyCode,
                            onValueChange = onVerifyCodeInput,
                            modifier = Modifier
                                .height(45.wdp)
                                .width(180.wdp),
                            placeText = "输入验证码",
                            isError = validationResults.verifyCodeError != null,
                            errorMessage = null
                        )
                        Spacer(modifier = Modifier.width(8.wdp))
                        NovelImageView(
                            imageUrl = captchaState.imagePath,
                            loadingStrategy = ImageLoadingStrategy.TEMPORARY,
                            useAdvancedCache = false,
                            isLoading = captchaState.isLoading,
                            error = captchaState.error,
                            widthDp = 100,
                            heightDp = 45,
                            modifier = Modifier.debounceClickable(onClick = onRefreshCaptcha),
                            onRetry = onRefreshCaptcha
                        )
                    }
                    
                    // 验证码错误信息区域 - 使用Box避免弹跳效果
                    Box(
                        modifier = Modifier.height(16.wdp).fillMaxWidth()
                    ) {
                        // 验证码错误信息 - 显示在输入框下方
                        if (validationResults.verifyCodeError != null) {
                            NovelText(
                                text = validationResults.verifyCodeError,
                                color = NovelColors.NovelError,
                                fontSize = 10.ssp,
                                lineHeight = 10.ssp,
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(start = 8.wdp)
                            )
                        }
                    }
                }
            }
    }
}