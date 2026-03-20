package com.novel.page.login

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.novel.page.component.NovelDivider
import com.novel.page.login.component.ActionButtons
import com.novel.page.login.component.AgreementSection
import com.novel.page.login.component.LoginAppBar
import com.novel.page.login.component.OperatorSection
import com.novel.page.login.component.PhoneSection
import com.novel.page.login.component.TitleSection
import com.novel.page.login.skeleton.LoginPageSkeleton
import com.novel.page.login.viewmodel.LoginEffect
import com.novel.page.login.viewmodel.LoginIntent
import com.novel.page.login.viewmodel.LoginViewModel
import com.novel.ui.theme.NovelColors
import com.novel.utils.NavViewModel
import com.novel.utils.NavViewModel.navController
import com.novel.utils.wdp
import kotlinx.coroutines.flow.collectLatest
import com.novel.page.login.component.InputSection

// 动画常量
private object AnimationConfig {
    const val DURATION_MS = 600
    const val DELAY_MS = 200
    const val BUTTON_DURATION_MS = 800
    val SPRING_SPEC = spring<Dp>(
        stiffness = Spring.StiffnessLow,
        dampingRatio = Spring.DampingRatioMediumBouncy
    )
}

/**
 * 登录页面 - MVI架构版本
 *
 * 性能优化特性：
 * - 使用优化的@Composable状态访问方法提升skippable比例
 * - 细粒度状态订阅，避免不必要的重组
 * - 稳定的回调函数缓存
 */
@SuppressLint("UnrememberedMutableState", "UseOfNonLambdaOffsetOverload")
@Composable
fun LoginPage() {
    val vm: LoginViewModel = hiltViewModel()
    val adapter = vm.adapter

    // 性能优化：使用优化的@Composable状态访问方法替代unstable的createStableState
    val isLoading by adapter.createLoadingState()
    val isLoginMode by adapter.isLoginModeState()
    val isAgreementAccepted by adapter.isAgreementAcceptedState()
    val operatorName by adapter.operatorNameState()
    val maskedPhoneNumber by adapter.maskedPhoneNumberState()
    val loginForm by adapter.loginFormState()
    val registerForm by adapter.registerFormState()
    val validationResults by adapter.validationResultsState()
    val captchaState by adapter.captchaStateState()
    val isSubmitEnabled by adapter.isSubmitEnabledState()
    val submitButtonText by adapter.submitButtonTextState()
    val switchModeButtonText by adapter.switchModeButtonTextState()

    // 弹跳动画状态管理
    var shouldTriggerBounce by remember { mutableStateOf(false) }

    // 收集副作用
    LaunchedEffect(Unit) {
        vm.effect.collectLatest { effect ->
            when (effect) {
                is LoginEffect.ShowToast -> {
                    // 登录失败时触发弹跳动画
                    shouldTriggerBounce = true
                }
                is LoginEffect.NavigateToHome ->
                    navController.value?.navigate("home")

                is LoginEffect.LaunchTelService -> {}
                is LoginEffect.NavigateToPrivacyPolicy ->
                    navController.value?.navigate("userAgreement")

                is LoginEffect.NavigateToTermsOfService ->
                    navController.value?.navigate("registerAgreement")

                is LoginEffect.FocusSmsCodeInput -> {}
                is LoginEffect.NavigateBack -> {
                    NavViewModel.navigateBack()
                }
                is LoginEffect.ShowSmsCodeSentDialog -> {}
                is LoginEffect.TriggerHapticFeedback -> {}
            }
        }
    }

    // 显示骨架屏或正常内容
    if (isLoading) {
        LoginPageSkeleton()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = NovelColors.NovelBackground),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginAppBar()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize(animationSpec = tween(durationMillis = 300))
                    .padding(top = 60.wdp)
                    .background(color = NovelColors.NovelBackground),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 标题
                TitleSection()

                NovelDivider(
                    modifier = Modifier.padding(horizontal = 22.5.wdp)
                )

                //  运营商
                OperatorSection(operatorName + "认证")

                // 脱敏后手机号
                PhoneSection(maskedPhoneNumber)

                // 性能优化：缓存输入回调，避免每次重组都创建新 Lambda
                val onPhoneInput = remember(vm) {
                    { phone: String ->
                        vm.sendIntent(LoginIntent.InputPhone(phone))
                    }
                }

                val onPasswordInput = remember(vm) {
                    { password: String ->
                        vm.sendIntent(LoginIntent.InputPassword(password))
                    }
                }

                val onPasswordConfirmInput = remember(vm) {
                    { passwordConfirm: String ->
                        vm.sendIntent(LoginIntent.InputPasswordConfirm(passwordConfirm))
                    }
                }

                val onVerifyCodeInput = remember(vm) {
                    { code: String ->
                        vm.sendIntent(LoginIntent.InputVerifyCode(code))
                    }
                }

                val onRefreshCaptcha = remember(vm) {
                    {
                        vm.sendIntent(LoginIntent.RefreshCaptcha) ?: Unit
                    }
                }

                // 输入框
                InputSection(
                    isLoginMode = isLoginMode,
                    loginForm = loginForm,
                    registerForm = registerForm,
                    validationResults = validationResults,
                    captchaState = captchaState,
                    onPhoneInput = onPhoneInput,
                    onPasswordInput = onPasswordInput,
                    onPasswordConfirmInput = onPasswordConfirmInput,
                    onVerifyCodeInput = onVerifyCodeInput,
                    onRefreshCaptcha = onRefreshCaptcha
                )

                // 构建注册模式切换的 Transition
                val transition =
                    updateTransition(
                        targetState = !isLoginMode,
                        label = "registerTransition"
                    )

                // 按钮区也用同样的弹性位移 + 透明度
                val buttonOffset by transition.animateDp(
                    transitionSpec = {
                        AnimationConfig.SPRING_SPEC
                    },
                    label = "buttonOffset"
                ) { state ->
                    if (state) 20.wdp else 0.wdp
                }
                val buttonAlpha by transition.animateFloat(
                    transitionSpec = {
                        tween(
                            durationMillis = AnimationConfig.BUTTON_DURATION_MS,
                            easing = FastOutSlowInEasing
                        )
                    },
                    label = "buttonAlpha"
                ) { state ->
                    if (state) 1f else 1f
                }

                // 按钮区：在登录/注册两种模式下都保持可见，但切换时做淡入淡出+展开收缩
                // 性能优化：缓存按钮回调，避免每次重组都创建新 Lambda
                val onFirstClick = remember(vm, isLoginMode) {
                    {
                        if (!isLoginMode) {
                            vm.sendIntent(LoginIntent.SubmitRegister) ?: Unit
                        } else {
                            vm.sendIntent(LoginIntent.SubmitLogin) ?: Unit
                        }
                    }
                }

                val onSecondClick = remember(vm, isLoginMode) {
                    {
                        if (isLoginMode) {
                            vm.sendIntent(LoginIntent.SwitchToRegister) ?: Unit
                        } else {
                            vm.sendIntent(LoginIntent.SwitchToLogin) ?: Unit
                        }
                    }
                }

                ActionButtons(
                    firstText = submitButtonText,
                    secondText = switchModeButtonText,
                    onFirstClick = onFirstClick,
                    isFirstEnabled = isSubmitEnabled,
                    onSecondClick = onSecondClick,
                    shouldTriggerBounce = shouldTriggerBounce,
                    onBounceComplete = { shouldTriggerBounce = false },
                    modifier = Modifier
                        .offset(y = buttonOffset)
                        .alpha(buttonAlpha)
                        .fillMaxWidth()
                        .padding(horizontal = 22.5.wdp)
                )

                //  协议
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
                    // 性能优化：缓存协议相关回调，避免每次重组都创建新 Lambda
                    val onCheckedChange = remember(vm) {
                        { accepted: Boolean ->
                            vm.sendIntent(LoginIntent.ToggleAgreement(accepted)) ?: Unit
                        }
                    }

                    val onTelServiceClick = remember(vm) {
                        {
                            vm.sendIntent(LoginIntent.NavigateToTelService) ?: Unit
                        }
                    }

                    val onUserAgreementClick = remember(vm) {
                        {
                            vm.sendIntent(LoginIntent.NavigateToPrivacyPolicy) ?: Unit
                        }
                    }

                    val onRegisterAgreementClick = remember(vm) {
                        {
                            vm.sendIntent(LoginIntent.NavigateToTermsOfService) ?: Unit
                        }
                    }

                    AgreementSection(
                        operator = operatorName,
                        isChecked = isAgreementAccepted,
                        onCheckedChange = onCheckedChange,
                        onTelServiceClick = onTelServiceClick,
                        onUserAgreementClick = onUserAgreementClick,
                        onRegisterAgreementClick = onRegisterAgreementClick,
                        modifier = Modifier
                            .offset(y = buttonOffset)
                            .padding(top = 16.wdp)
                    )
                }
            }
        }
    }
}
