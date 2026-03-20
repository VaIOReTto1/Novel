package com.novel.page.component.pagecurl.page

/**
 * 标记实验性的PageCurl API
 * 
 * 使用此注解标记的API可能在未来版本中发生变化或被移除
 * 主要用于PageCurl组件的高级功能和内部实现
 * 
 * @author Novel Team
 * @since 1.0
 */
@RequiresOptIn(
    message = "This is an experimental API for PageCurl and may change in the future.",
    level = RequiresOptIn.Level.WARNING
)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.TYPEALIAS
)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalPageCurlApi 
