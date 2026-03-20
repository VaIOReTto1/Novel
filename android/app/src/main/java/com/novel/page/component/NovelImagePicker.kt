package com.novel.page.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.novel.utils.TimberLogger
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 图片选择器组件
 * 
 * 功能：
 * - 启动系统图片选择Intent
 * - 异步图片解码处理
 * - Bitmap回调上报
 * 
 * 特点：
 * - 完全无状态设计
 * - 无需额外权限
 * - 协程异步加载
 * - 单一职责原则
 * 
 * @param isPresented 是否显示选择器
 * @param onImagePicked 图片选择回调
 * @param onDismiss 取消选择回调
 */
@Composable
fun NovelImagePickerLauncher(
    isPresented: Boolean,
    onImagePicked: (Bitmap) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // 注册系统图片选择回调
    val launcher = rememberLauncherForActivityResult(GetContent()) { uri: Uri? ->
        uri?.let {
            TimberLogger.d("NovelImagePicker", "图片选择成功: $uri")
            scope.launch {
                // IO线程解码Bitmap
                val bmp = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(it)
                        ?.use { stream -> BitmapFactory.decodeStream(stream) }
                }
                bmp?.let { bitmap ->
                    TimberLogger.d("NovelImagePicker", "Bitmap解码成功: ${bitmap.width}x${bitmap.height}")
                    onImagePicked(bitmap)
                } ?: run {
                    TimberLogger.w("NovelImagePicker", "Bitmap解码失败")
                }
            }
        } ?: run {
            TimberLogger.d("NovelImagePicker", "用户取消图片选择")
        }
        onDismiss()
    }

    // 当isPresented=true时自动调起选图
    LaunchedEffect(isPresented) {
        if (isPresented) {
            TimberLogger.d("NovelImagePicker", "启动图片选择器")
            launcher.launch("image/*")
        }
    }
}
