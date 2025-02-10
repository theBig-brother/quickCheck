package com.lzf.quickcheck.screens.functions

import androidx.compose.runtime.Composable
import android.os.Environment
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.io.File

@Composable
fun Picture(navController: NavController, imagePath: String) {
    // 根据传入的图片路径创建文件对象
    val imageFile = File(imagePath)

    // 如果文件存在，加载图片
    if (imageFile.exists()) {
        val imageBitmap = remember(imageFile) {
            // 使用 BitmapFactory 解码文件为 Bitmap，并转换为 ImageBitmap
            val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            bitmap?.asImageBitmap() // 转换为 ImageBitmap
        }

        imageBitmap?.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 显示大图
                Image(
                    bitmap = it, // 使用加载的 ImageBitmap
                    contentDescription = "Full Image", // 图片的描述信息
                    modifier = Modifier.fillMaxSize() // 图片填充整个屏幕
                )

                Spacer(modifier = Modifier.height(16.dp))
                // 显示图片的名称或信息
                BasicText(
                    text = "Image: ${imageFile.name}",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 返回按钮，点击后返回到图片列表页面
                Button(onClick = { navController.popBackStack() }) {
                    Text("Back to Pictures")
                }
            }
        }
    } else {
        // 如果图片文件不存在，显示错误提示
        Text(text = "Image not found", modifier = Modifier.fillMaxSize())
    }
}

@Preview
@Composable
fun PreviewPictureDetailPage() {
    val navController = rememberNavController()
    val imagePath = "path/to/your/image.jpg" // 使用一个示例路径
    Picture(navController = navController, imagePath = imagePath)
}
