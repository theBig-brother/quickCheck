package com.lzf.quickcheck.screens.functions

import android.os.Environment
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import java.io.File

@Composable
fun Pictures(navController: NavController) {
    // 获取图片目录路径
    val pictureFolder = File("${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)}/QuickCheck")

    // 检查目录是否存在，并获取所有图片文件
    var imageFiles by remember(pictureFolder) {
        mutableStateOf(
            if (pictureFolder.exists() && pictureFolder.isDirectory) {
                pictureFolder.listFiles()?.filter { it.isFile && it.extension in listOf("jpg", "jpeg", "png", "gif") } ?: emptyList()
            } else {
                emptyList<File>()
            }
        )
    }

    // 如果图片文件为空，显示提示
    if (imageFiles.isEmpty()) {
        Text(text = "No images found") // 显示没有找到图片的提示
        return // 退出当前Composable，不继续执行下面的代码
    }

    // 记录当前弹窗的图片
    var selectedImage by remember { mutableStateOf<File?>(null) }
    // 控制删除确认弹框
    var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
    var imageToDelete by remember { mutableStateOf<File?>(null) }
    // 控制大图弹窗的显示
    var showImageDialog by remember { mutableStateOf(false) }

    // 显示图片
    LazyVerticalGrid(
        columns = GridCells.Fixed(4), // 每行显示 4 张图片
        modifier = Modifier.fillMaxSize() // 填充整个可用空间
    ) {
        items(imageFiles.size) { index -> // 遍历所有图片文件
            val imageFile = imageFiles[index] // 获取当前图片文件
            val imageBitmap = remember(imageFile) {
                // 使用 BitmapFactory 解码文件为 Bitmap，并转换为 ImageBitmap
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                bitmap?.asImageBitmap() // 转换为 ImageBitmap
            }

            Card(
                modifier = Modifier
                    .padding(4.dp) // 设置每张卡片的间距
                    .clickable {
                        // 点击图片，显示弹窗
                        selectedImage = imageFile
                        showImageDialog = true
                    }
            ) {
                // 显示解码后的图片
                imageBitmap?.let {
                    Image(
                        bitmap = it, // 使用加载的 ImageBitmap
                        contentDescription = "Image ${imageFile.name}", // 图片的描述信息
                        modifier = Modifier.fillMaxSize() // 图片填充整个卡片
                    )
                }
            }
        }
    }

    // 如果有选择图片，显示大图弹窗
    if (showImageDialog && selectedImage != null) {
        ImageDialog(
            imageFile = selectedImage!!,
            onDismiss = {
                showImageDialog = false
                selectedImage = null
            },
            onDelete = { file ->
                // Show confirmation dialog for delete
                imageToDelete = file
                showDeleteConfirmationDialog = true
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmationDialog = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this image?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        imageToDelete?.let { file ->
                            // Delete image file
                            val deleted = file.delete()
                            if (deleted) {
                                // Remove deleted image from the list
                                imageFiles = imageFiles.filterNot { it == file }
                            }
                        }
                        // Close the delete confirmation dialog
                        showDeleteConfirmationDialog = false
                        // Close the image dialog as well after deletion
                        showImageDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmationDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ImageDialog(imageFile: File, onDismiss: () -> Unit, onDelete: (File) -> Unit) {
    // Dialog组件用于展示大图
    AlertDialog(
        onDismissRequest = onDismiss, // 关闭弹窗的处理
        title = null,
        text = {
            val imageBitmap = remember(imageFile) {
                // 使用 BitmapFactory 解码文件为 Bitmap，并转换为 ImageBitmap
                val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                bitmap?.asImageBitmap() // 转换为 ImageBitmap
            }

            imageBitmap?.let {
                Image(
                    bitmap = it, // 使用加载的 ImageBitmap
                    contentDescription = "Full Image", // 图片的描述信息
                    modifier = Modifier.fillMaxSize() // 图片填充整个屏幕
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close") // 关闭弹窗按钮
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = { /* Implement send functionality here */ }) {
                    Text("Send") // 发送按钮
                }
                TextButton(onClick = { onDelete(imageFile) }) {
                    Text("Delete") // 删除按钮
                }
            }
        }
    )
}

@Preview
@Composable
fun PreviewPictures() {
    // 传入一个假的NavController来预览
    val navController = rememberNavController() // 创建一个用于预览的NavController实例
    Pictures(navController = navController) // 调用Pictures函数来进行预览
}
