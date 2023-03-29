/*
 * MIT License
 *
 * Copyright (c) 2021 Vast Gui
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.pluginversion.vastgui.*

plugins{
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.pluginversion.vastgui")
}


android {
    compileSdk = Version.compile_sdk_version

    defaultConfig {
        applicationId = "com.gcode.gweather"
        minSdk = Version.min_sdk_version
        targetSdk = Version.target_sdk_version
        versionCode = Version.version_code
        versionName = Version.version_name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            // 选择要添加的对应cpu类型的.so库,只支持七种 mips,
            // mips64, X86, X86_64, arm64-v8a, armeabi, armeabi-v7a
            // 参考: https://mp.weixin.qq.com/s/jnZpgaRFQT5ULk9tHWMAGg
            abiFilters.add("arm64-v8a")
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = File("D:\\AndroidKey\\XiaFengWeather.jks")
            storePassword = project.property("myStorePassword") as String?
            keyPassword = project.property("myKeyPassword") as String?
            keyAlias = project.property("myKeyAlias") as String?
        }
        create("release") {
            storeFile = File("D:\\AndroidKey\\XiaFengWeather.jks")
            storePassword = project.property("myStorePassword") as String?
            keyPassword = project.property("myKeyPassword") as String?
            keyAlias = project.property("myKeyAlias") as String?
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            isJniDebuggable = false
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
        }
        debug {
            isDebuggable = true
            isJniDebuggable = true
            isShrinkResources = false
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = Version.java_version
        targetCompatibility = Version.java_version
    }

    kotlinOptions {
        jvmTarget = Version.java_version.toString()
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    namespace = "com.gcode.gweather"

    sourceSets["main"].java.srcDir("src/main/kotlin")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs","include" to listOf("*.jar"))))

    implementation(AVE.tools)

    implementation(AndroidX.activity_ktx)
    implementation(AndroidX.core_ktx)
    implementation(AndroidX.appcompat)
    implementation(AndroidX.recyclerview)
    implementation(AndroidX.cardview)
    implementation(AndroidX.constraintlayout)
    implementation(AndroidX.viewpager2)
    implementation(AndroidX.annotation)
    implementation(AndroidX.lifecycle_viewmodel_ktx)
    implementation(AndroidX.lifecycle_livedata_ktx)
    androidTestImplementation(AndroidX.junit)
    androidTestImplementation(AndroidX.espresso_core)
    debugImplementation(AndroidX.fragment_testing)

    implementation(Google.material)
    implementation(Google.gson)

    implementation(Jetbrains.kotlinx_coroutines_core)
    implementation(Jetbrains.kotlinx_coroutines_android)

    implementation(Squareup.okhttp3)
    implementation(Squareup.retrofit2)
    implementation(Squareup.retrofit2_converter_gson)

    implementation(Libraries.animatedbottombar)
    implementation(Libraries.donut)
    implementation(Libraries.gradienttext)
    implementation(Libraries.permissionx)
    implementation(Libraries.circleimageview)
    implementation(Libraries.navi_3dmap)
    implementation(Libraries.aachartcore_kotlin)
    implementation(Libraries.refresh_layout_kernel)
    implementation(Libraries.refresh_header_radar)
    implementation(Libraries.vastadapter)
    testImplementation(Libraries.junit)
}