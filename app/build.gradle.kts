plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.lzf.quickcheck"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lzf.quickcheck"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }




    buildTypes {
        release {
            // 启用代码缩减、混淆和优化
            isMinifyEnabled = true
            // 启用资源缩减
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = "1.5.0"
    externalNativeBuild {
        cmake {
            version = "3.10.2"
            path = file("src/main/jni/CMakeLists.txt")
        }
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    //Icon
    implementation("androidx.compose.material:material-icons-extended")
    //权限控制
    implementation("com.google.accompanist:accompanist-permissions:0.24.7-alpha")
//Retrofit HTTP
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // 用于解析JSON

    // Jetpack Compose 依赖（核心）
    implementation("androidx.activity:activity-compose:1.7.2") // 支持 Compose 版 Activity
    implementation("androidx.compose.ui:ui:1.5.0") // 更新为与 Kotlin Compiler Extension 兼容的版本
    implementation("androidx.compose.material3:material3:1.1.2") // Material3 组件库（按钮、卡片等）
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2") // 支持 Compose 版生命周期管理

    implementation(libs.androidx.ui.tooling.preview.android)
    // ✅ 解决 Navigation 问题
    implementation(libs.androidx.navigation.compose) // Jetpack Compose Navigation

    // CameraX 依赖（用于简化相机操作）
    implementation("androidx.camera:camera-core:1.4.1") // CameraX 核心库
    implementation("androidx.camera:camera-camera2:1.4.1") // 基于 Camera2 API 的 CameraX 适配器
    implementation("androidx.camera:camera-lifecycle:1.4.1") // 让 CameraX 绑定生命周期，自动管理相机状态
    implementation(libs.androidx.camera.view) // 提供 CameraX 预览控件（PreviewView）
//    添加 Coil 的依赖
    implementation("io.coil-kt:coil-compose:2.2.2") // 使用适当的版本号
}