package com.lzf.quickcheck.screens.home

import Yolo // 引入Yolo类，用于图像处理
import android.content.ContentValues // 引入用于存储媒体信息的ContentValues
import android.content.Context // 引入Context类，用于获取应用上下文
import android.graphics.Bitmap // 引入Bitmap类，表示图片数据
import android.graphics.BitmapFactory
import android.net.Uri // 引入Uri类，表示图片文件的URI
import android.os.Build
import android.os.Environment // 引入Environment类，获取设备的公共存储路径
import android.provider.MediaStore // 引入MediaStore类，用于访问媒体存储
import android.util.Log // 引入Log类，用于日志记录
import android.widget.Toast // 引入Toast类，用于弹出提示信息
import androidx.annotation.RequiresApi
import androidx.camera.core.* // 引入CameraX相关类，用于摄像头操作
import androidx.camera.lifecycle.ProcessCameraProvider // 引入ProcessCameraProvider类，管理CameraX生命周期
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.* // 引入布局类，提供行、列、框等布局组件
import androidx.compose.material.icons.Icons // 引入Material Icons
import androidx.compose.material.icons.filled.CameraAlt // 引入相机图标
import androidx.compose.material.icons.filled.CameraRear
import androidx.compose.material.icons.filled.Videocam // 引入摄像机图标
import androidx.compose.material3.FloatingActionButton // 引入浮动按钮
import androidx.compose.material3.Icon // 引入Icon类，用于显示图标
import androidx.compose.runtime.* // 引入Compose的状态管理功能
import androidx.compose.ui.Alignment // 引入Alignment类，用于设置布局对齐
import androidx.compose.ui.Modifier // 引入Modifier类，用于修饰UI组件
import androidx.compose.ui.platform.LocalContext // 引入LocalContext，用于访问应用上下文
import androidx.compose.ui.unit.dp // 引入dp单位，设置组件大小
import androidx.compose.ui.viewinterop.AndroidView // 引入AndroidView，集成Android视图组件
import androidx.lifecycle.LifecycleOwner // 引入LifecycleOwner接口，管理组件生命周期
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController // 引入NavController，管理页面导航
import kotlinx.coroutines.* // 引入协程库，进行异步处理
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.IOException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ExecutorService // 引入ExecutorService，用于线程池管理
import java.util.concurrent.Executors // 引入Executors类，用于创建线程池


@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun CameraScreen(navController: NavController) { // Composable函数，表示一个界面
    val context = LocalContext.current // 获取当前的上下文
    val lifecycleOwner = LocalContext.current as LifecycleOwner // 获取生命周期拥有者
    val cameraProviderFuture =
        remember { ProcessCameraProvider.getInstance(context) } // 初始化CameraX提供者
    val executor = remember { Executors.newSingleThreadExecutor() } // 创建单线程执行器
//    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) } // 定义ImageCapture对象用于拍照
    var imageCapture=  ImageCapture.Builder()
        .setTargetRotation(context.resources.configuration.orientation) // 设置目标方向
        .build()
    var isAutoCapture by remember { mutableStateOf(false) } // 自动拍照开关
    val coroutineScope = rememberCoroutineScope() // 创建协程作用域
    val videoCapture: MutableState<VideoCapture<Recorder>?> = remember { mutableStateOf(null) }
    val previewView: PreviewView = remember { PreviewView(context) }
    val cameraSelector: MutableState<CameraSelector> = remember {
        mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    }
    LaunchedEffect(previewView) {
        imageCapture = context.createImageCaptureUseCase(
            lifecycleOwner = lifecycleOwner,
            cameraSelector = cameraSelector.value,
            previewView = previewView
        )!!
    }

 Box(modifier = Modifier.fillMaxSize()) {
      // 创建一个填满屏幕的容器
        AndroidView(
            modifier = Modifier.fillMaxSize(), // 填满父容器
            factory = {
                previewView // 返回预览视图
            }
        )
        // 拍照按钮
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // 填充父容器并设置内边距
            verticalArrangement = Arrangement.Bottom, // 垂直方向底部对齐
            horizontalAlignment = Alignment.CenterHorizontally // 水平方向居中对齐
        ) {
            Row {
                // 自动拍照按钮
                FloatingActionButton(
                    onClick = {
                        isAutoCapture = !isAutoCapture // 切换自动拍照状态
                        if (isAutoCapture) {
                            Toast.makeText(context, "Auto capture started", Toast.LENGTH_SHORT)
                                .show() // 弹出提示信息
                        } else {
                            Toast.makeText(context, "Auto capture stopped", Toast.LENGTH_SHORT)
                                .show() // 弹出提示信息
                        }
                    },
                    modifier = Modifier.padding(end = 16.dp) // 设置按钮右边距
                ) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Videocam, // 显示摄像机图标
                        contentDescription = "Auto Capture" // 图标的描述
                    )
                }
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
                            imageCapture = context.createImageCaptureUseCase(
                                lifecycleOwner = lifecycleOwner,
                                cameraSelector = cameraSelector.value,
                                previewView = previewView
                            )!!
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
    // 使用 DisposableEffect 释放资源
    DisposableEffect(lifecycleOwner) {
        onDispose {
            // 销毁相机资源
            cameraProviderFuture.get().unbindAll() // 释放相机资源
        }
    }
    LaunchedEffect(isAutoCapture) {
        // 如果开启了自动拍照，每秒拍摄一张
        while (isAutoCapture) {
            takePhoto(imageCapture, context, executor) // 调用拍照函数
            delay(1000)  // 每秒拍摄一张
        }
    }
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
            output.savedUri?.let { uri -> // 获取保存的图片URI
//                processAndUploadImage(uri, context) // 处理并上传图片
                // 将图片转换为 Bitmap
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close() // 关闭流
                // 处理图片
                val result = Yolo(context).processImage(bitmap)
                // 获取处理后的图片
                val processedBitmap = result.first
                // 直接保存处理后的图片到图库
                // 注意：此时我们只保存处理后的图像，而不再保存原始图像
                saveImageToGallery(processedBitmap, context,uri)
            }
        }
        override fun onError(exception: ImageCaptureException) {
            Log.e("CameraScreen", "Photo capture failed: ${exception.message}") // 错误日志
        }
    })
}
fun saveProcessedImage(bitmap: Bitmap, originalUri: Uri) {
    try {
        // 获取原图片的文件路径
        val file = originalUri.path?.let { File(it) }

        // 如果文件已经存在，先删除它
        if (file != null) {
            if (file.exists()) {
                file.delete()
            }
        }

        // 创建一个新的文件并使用 FileOutputStream 写入处理后的图像
        val outputStream = FileOutputStream(file)

        // 压缩并保存图片
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        // 刷新并关闭输出流
        outputStream.flush()
        outputStream.close()

        Log.d("CameraScreen", "Processed image saved successfully, overwriting original image.")
    } catch (e: IOException) {
        // 处理保存失败的异常
        Log.e("CameraScreen", "Failed to save processed image: ${e.message}")
    }
}


// 读取 Bitmap
private fun uriToBitmap(uri: Uri, context: Context): Bitmap? {
    return try {
        context.contentResolver.openInputStream(uri)?.use { inputStream -> // 打开输入流
            android.graphics.BitmapFactory.decodeStream(inputStream) // 解码流为Bitmap
        }
    } catch (e: Exception) {
        Log.e("CameraScreen", "Failed to convert URI to Bitmap: ${e.message}") // 错误日志
        null
    }
}

// 保存到本地相册
private fun saveImageToGallery(bitmap: Bitmap, context: Context,originalUri: Uri) {
    val fileUri = originalUri // 原始的文件 Uri

// 如果Uri不为null，尝试删除
    val contentResolver = context.contentResolver
    val rowsDeleted = contentResolver.delete(fileUri, null, null)

    if (rowsDeleted > 0) {
        Log.e("FILEdelete", "File deleted successfully")
    } else {
        Log.e("FILEdelete", "File deletion failed or file not found")
    }


    val contentValues = ContentValues().apply {
        put(
            MediaStore.Images.Media.DISPLAY_NAME,
            "Processed_${System.currentTimeMillis()}.jpg"
        ) // 设置文件名
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg") // 设置文件类型
        put(
            MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + "/quickCheck"
        ) // 设置保存路径
    }

    val uri =
        context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ) // 插入媒体库
    uri?.let {
        context.contentResolver.openOutputStream(it)?.use { outputStream -> // 打开输出流
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream) // 保存图像为JPEG
        }
    }
}

interface ApiService {
    @Multipart
    @POST("upload") // 服务器上传的 URL 路径
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<ResponseBody>
}
// 处理并上传图像
private fun processAndUploadImage(uri: Uri, context: Context) {
    val bitmap = uriToBitmap(uri, context) ?: return // 获取Bitmap
    // 使用YOLO处理图像
    val result = Yolo(context).processImage(bitmap)
    val processedBitmap = result.first
    val hasPerson = result.second

    saveImageToGallery(processedBitmap, context,uri) // 保存处理后的图片
//    uploadToServer(uri, context) // 上传图片到服务器
}

// 上传到服务器
private fun uploadToServer(uri: Uri, context: Context) {
    // 将 URI 转换为 Bitmap 对象，如果失败则返回
    val bitmap = uriToBitmap(uri, context) ?: return

    // 创建一个 ByteArrayOutputStream，用于将 Bitmap 压缩为字节数组
    val byteArrayOutputStream = ByteArrayOutputStream()

    // 将 Bitmap 压缩为 JPEG 格式，并将压缩后的数据写入 ByteArrayOutputStream
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

    // 获取压缩后的字节数组
    val byteArray = byteArrayOutputStream.toByteArray()

    // 创建 RequestBody 对象，用于向服务器发送 JPEG 格式的图片数据
    val requestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), byteArray)

    // 创建 MultipartBody.Part 用于上传图片
    val part = MultipartBody.Part.createFormData("file", "image.jpg", requestBody)

    // 创建 Retrofit 实例，指定基础 URL 和 JSON 转换器
    val retrofit = Retrofit.Builder()
        .baseUrl("http://localhost:8080/")  // 设置基础 URL
        .addConverterFactory(GsonConverterFactory.create())  // 使用 Gson 转换器解析 JSON 数据
        .build()

    // 创建 ApiService 实例，定义 API 请求
    val apiService = retrofit.create(ApiService::class.java)

    // 使用协程在 IO 线程中进行网络请求
    CoroutineScope(Dispatchers.IO).launch {
        try {
            // 调用 API 上传图片数据
            val response = apiService.uploadImage(part)

            // 如果响应成功，则打印上传成功日志
            if (response.isSuccessful) {
                Log.d("CameraScreen", "Upload success")
            } else {
                // 如果响应失败，则打印失败的消息
                Log.e("CameraScreen", "Upload failed: ${response.message()}")
            }
        } catch (e: Exception) {
            // 如果发生异常，打印错误信息
            Log.e("CameraScreen", "Upload failed: ${e.message}")
        }
    }
}
