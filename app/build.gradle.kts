/*
 * MIT License
 *
 * Copyright (c) 2024 Vast Gui
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

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.xfw.vastgui"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isDebuggable = true
            isJniDebuggable = true
            isShrinkResources = false
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    namespace = "com.xfw.vastgui"

    sourceSets["main"].java.srcDir("src/main/kotlin")
}

dependencies {
    implementation(project(":qweather-ktx"))
    implementation(libs.vastadapter)
    implementation(libs.vasttools)
    implementation(libs.activity.ktx)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.constraintlayout)
    implementation(libs.viewpager2)
    implementation(libs.annotation)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.lifecycle.livedata.ktx)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    debugImplementation(libs.fragment.testing)
    implementation(libs.material)
    implementation(libs.gson)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.library)
    implementation(libs.gradienttext)
    implementation(libs.circleimageview)
    implementation(libs.location)
    implementation(libs.aachartcore)
    implementation(libs.refresh.layout.kernel)
    implementation(libs.refresh.header.radar)
    testImplementation(libs.junit.junit)
}