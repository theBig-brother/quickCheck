package com.lzf.quickcheck.screens.home

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.os.Build
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import java.io.ByteArrayOutputStream
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class cameraFunction {
}


@RequiresApi(Build.VERSION_CODES.P)
suspend fun Context.createImageCaptureUseCase(
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    previewView: PreviewView
): ImageCapture? {
    val preview = Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }

    val imageCapture =
        ImageCapture.Builder().setTargetRotation(previewView.display.rotation).build()

    val cameraProvider = getCameraProvider()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        imageCapture
    )
    return imageCapture
}


// 要求 Android 9.0（Pie）及以上版本
@RequiresApi(Build.VERSION_CODES.P)
// 声明一个挂起函数（扩展Context的成员函数），用于创建视频捕获用例
suspend fun Context.createVideoCaptureUseCase(
    lifecycleOwner: LifecycleOwner,  // 生命周期拥有者（如Activity/Fragment）
    cameraSelector: CameraSelector,  // 相机选择器（前后置摄像头选择）
    previewView: PreviewView          // 预览视图容器
): VideoCapture<Recorder> {          // 返回视频捕获用例对象（带Recorder输出）

    // 构建预览用例 -------------------------------------------------
    val preview = Preview.Builder()  // 创建预览配置构造器
        .build()                      // 构建Preview用例实例
        .apply {
            // 将预览的SurfaceProvider设置给预览视图
            setSurfaceProvider(previewView.surfaceProvider)
        }

    // 配置视频质量选择器 --------------------------------------------
    val qualitySelector = QualitySelector.from(
        Quality.FHD,  // 首选1080P分辨率
        // 质量回退策略：如果无法达到FHD，选择更低或更高分辨率
        FallbackStrategy.lowerQualityOrHigherThan(Quality.FHD)
    )

    // 构建视频录制器 -----------------------------------------------
    val recorder = Recorder.Builder()
        .setExecutor(mainExecutor)        // 设置主线程执行器
        .setQualitySelector(qualitySelector)  // 应用质量选择器
        .build()                          // 创建Recorder实例

    // 创建视频捕获用例 ---------------------------------------------
    val videoCapture = VideoCapture.withOutput(recorder)  // 绑定录制器作为输出

    // 获取相机提供者并绑定用例 --------------------------------------
    val cameraProvider = getCameraProvider()  // 假设这是获取ProcessCameraProvider的挂起函数
    cameraProvider.unbindAll()          // 解除之前绑定的所有用例
    cameraProvider.bindToLifecycle(     // 将用例绑定到生命周期
        lifecycleOwner,                 // 生命周期拥有者
        cameraSelector,                 // 选择的摄像头
        preview,                        // 预览用例
        videoCapture                   // 视频捕获用例
    )

    return videoCapture  // 返回创建的视频捕获用例
}
// 声明一个挂起函数，这个函数在 Context 上扩展，返回一个 ProcessCameraProvider 对象
@RequiresApi(Build.VERSION_CODES.P)
suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { continuation ->

    // 获取 ProcessCameraProvider 的实例，返回一个 Future 对象
    ProcessCameraProvider.getInstance(this).also { future ->

        // 为 future 对象添加监听器，当 future 完成时会调用传入的代码块
        future.addListener(
            {
                // 当 future 获取到结果时，调用 continuation.resume() 恢复挂起的协程，并返回获取到的结果
                continuation.resume(future.get())
            },
            mainExecutor // 使用主线程的 Executor 来执行这个回调
        )
    }
}
fun ImageProxy.toBitmap(): Bitmap {
    // 获取 YUV 数据
    val buffer = this.planes[0].buffer
    val bytes = ByteArray(buffer.remaining())
    buffer.get(bytes)

    // 创建 YuvImage 对象，并转换为 JPEG 格式
    val yuvImage = YuvImage(bytes, ImageFormat.NV21, this.width, this.height, null)
    val outStream = ByteArrayOutputStream()

    // 将 YUV 数据压缩为 JPEG 格式
    yuvImage.compressToJpeg(Rect(0, 0, this.width, this.height), 100, outStream)
    val jpegByteArray = outStream.toByteArray()

    // 使用 BitmapFactory 解码 JPEG 数据
    return BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.size)
}
