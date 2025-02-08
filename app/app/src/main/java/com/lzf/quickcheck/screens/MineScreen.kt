package com.lzf.quickcheck.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.lzf.quickcheck.R // 确保你有一个头像图片资源（可替换成自己的图片）

@Composable
fun MineScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 用户头像和名字区域
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            // 头像
            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(50)),
                shape = RoundedCornerShape(50),
                color = MaterialTheme.colorScheme.surface
            ) {
                // 替换成你自己的头像图片（或者使用一个占位图）
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_background), // 请替换为真实的资源
                    contentDescription = "Avatar",
                    modifier = Modifier.size(80.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))

            // 用户信息（名称和状态）
            Column {
//                Text("用户名", fontSize = 20.sp, fontWeight = MaterialTheme.typography.h6.fontWeight)
                Text("用户名", fontSize = 20.sp, fontWeight= FontWeight.W300)
                Text("VIP会员 | 等级 5", fontSize = 14.sp, color = Color.Gray)
            }
        }

        // 账户信息卡片（积分、余额等）
        InfoCard(
            title = "账户信息",
            details = "积分: 1000   |   余额: ¥120.50",
            onClick = { /* 点击账户信息，跳转到相关页面 */ }
        )

        // 功能区域（如设置、通知、帮助等）
        Spacer(modifier = Modifier.height(32.dp))
        SettingsSection()

        // 退出登录按钮
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { /* 退出登录 */ },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("退出登录", color = Color.White)
        }
    }
}

@Composable
fun InfoCard(title: String, details: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.W300)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = details, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SettingsSection() {
    Column {
        // 设置项：个人资料、修改密码等
        SettingItem(label = "个人资料", onClick = { /* 处理点击事件 */ })
        SettingItem(label = "通知设置", onClick = { /* 处理点击事件 */ })
        SettingItem(label = "帮助与反馈", onClick = { /* 处理点击事件 */ })
        SettingItem(label = "版本信息", onClick = { /* 处理点击事件 */ })
    }
}

@Composable
fun SettingItem(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, modifier = Modifier.padding(start = 16.dp), fontSize = 16.sp)
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = "Forward",
            modifier = Modifier.padding(end = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMineScreen() {
    MineScreen()
}
