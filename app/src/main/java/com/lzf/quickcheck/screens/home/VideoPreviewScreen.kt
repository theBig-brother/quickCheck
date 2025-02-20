package com.lzf.quickcheck.screens.home

//import android.graphics.Canvas

import Yolo
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CameraRear
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import android.graphics.Color
import android.graphics.Matrix
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun VideoPreviewScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) } // 定义ImageCapture对象用于拍照
    val previewView: PreviewView = remember { PreviewView(context) }
    val executor = remember { Executors.newSingleThreadExecutor() } // 创建单线程执行器
    val imageBitmap = remember { mutableStateOf<Bitmap?>(null) }// 用于显示绘制的图像
    val cameraSelector: MutableState<CameraSelector> = remember {
        mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    }
    val imageView = remember { ImageView(context) }
    LaunchedEffect(previewView) {
        imageCapture =
            context.createImageCaptureProcessed(
                lifecycleOwner = lifecycleOwner,
                cameraSelector = cameraSelector.value,
                previewView = previewView,
                imageBitmap = imageBitmap,
                context=context
            )
    }
    val transparentBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)

    val canvas = android.graphics.Canvas(transparentBitmap)

    // 在这里绘制方框
    val paint = Paint().apply {
        color = Color.rgb(139, 125, 96)
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    canvas.drawRect(0f, 0f, 50f, 50f, paint)  // 绘制方框，调整坐标和大小
    val isPreviewVisible = remember { mutableStateOf(true) } // 用来控制是否显示

    Box(modifier = Modifier.fillMaxSize()) {
        // 创建一个填满屏幕的容器

        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
                .alpha(if (isPreviewVisible.value) 1f else 0f)// touMingDu

        )
        // 如果有处理后的图像，显示在 PreviewView 上方

        val bitmap = imageBitmap.value?.asImageBitmap()

        bitmap?.let {
            Image(
                bitmap = it,
                contentDescription = "Overlay Canvas",
                modifier = Modifier.fillMaxSize().onGloballyPositioned { coordinates ->
                    // 获取 Image 组件的高度
                   val imageHeight = coordinates.size.height.toFloat()

                }, // 旋转90度
                contentScale = ContentScale.FillBounds // 使图像拉伸填充整个 Image
            )
        }


        // 在Box中绘制Canvas
//        Canvas(modifier = Modifier.fillMaxSize().fillMaxSize() // 覆盖整个视图
//            .absoluteOffset(x = 0.dp, y = 0.dp) // 设置定位位置，调整为你需要的值

        // 拍照按钮
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // 填充父容器并设置内边距
            verticalArrangement = Arrangement.Bottom, // 垂直方向底部对齐
            horizontalAlignment = Alignment.CenterHorizontally // 水平方向居中对齐
        ) {
            Row {

                // 手动拍照按钮
                FloatingActionButton(
                    onClick = {
                        Toast.makeText(context, "Taking photo...", Toast.LENGTH_SHORT)
                            .show() // 弹出拍照提示
                        takePhoto(imageCapture, context, executor) // 调用拍照函数
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraAlt, // 显示相机图标
                        contentDescription = "Capture" // 图标的描述
                    )
                }
                // 切换摄像头按钮
                FloatingActionButton(
                    onClick = {
                        cameraSelector.value =
                            if (cameraSelector.value == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
                            else CameraSelector.DEFAULT_BACK_CAMERA
                        lifecycleOwner.lifecycleScope.launch {
                            imageCapture = context.createImageCaptureProcessed(
                                lifecycleOwner = lifecycleOwner,
                                cameraSelector = cameraSelector.value,
                                previewView = previewView,
                                imageBitmap = imageBitmap,
                                context=context
                            )
                        }
                        Toast.makeText(context, "Switch photo...", Toast.LENGTH_SHORT)
                            .show() // 弹出切换提示
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.CameraRear, // 显示相机图标
                        contentDescription = "Switch" // 图标的描述
                    )
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.P)
suspend fun Context.createImageCaptureProcessed(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView,
    imageBitmap: MutableState<Bitmap?>,
    context:Context
): ImageCapture? {
    val preview = Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }
    previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
    val rotation = previewView.display?.rotation ?: Surface.ROTATION_0
    val imageCapture = ImageCapture.Builder()
        .setTargetRotation(rotation)
        .build()


    // 创建 ImageAnalysis 用于处理预览帧
    val imageAnalysis = ImageAnalysis.Builder()
        .setTargetResolution(Size(1920, 1080))  // 设置图像分辨率
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)  // 设置后压策略
        .build()

    // 设置分析器，分析每一帧的图像
    imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
        // 转换 ImageProxy 为 Bitmap
        val bitmap = imageProxy.toBitmap()
        val bitmap1=Yolo(context).onlyRect(bitmap)
//Log.w("bitmap1.h",bitmap1.first.height.toString())
//Log.w("bitmap1.w",bitmap1.first.width.toString())
        // 更新绘制后的 Bitmap
        imageBitmap.value = bitmap1.first
        // 一定要关闭 imageProxy
        imageProxy.close()
    }

    val cameraProvider = getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        imageCapture,
        imageAnalysis
    )
    return imageCapture
}

// 拍照并处理图像
private fun takePhoto(
    imageCapture: ImageCapture?, // 图像捕捉对象
    context: Context, // 应用上下文
    executor: ExecutorService // 执行器
) {

    // 设置输出文件选项
    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver, // 获取内容解析器
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // 存储路径
        ContentValues().apply {
            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                "IMG_${System.currentTimeMillis()}.jpg"
            ) // 设置文件名
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg") // 设置文件类型
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/QuickCheck" // 设置保存路径
            )
        }
    ).build()
    // 拍照并保存

    imageCapture?.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
            output.savedUri?.let { uri ->

            }
        }

        override fun onError(exception: ImageCaptureException) {
            Log.e("CameraScreen", "Photo capture failed: ${exception.message}") // 错误日志
        }
    })

}
