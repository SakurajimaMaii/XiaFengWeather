import com.gcode.plugin.version.*

plugins{
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.gcode.plugin.version")
}


android {
    compileSdk = Version.compile_sdk_version
    buildToolsVersion = Version.build_tools_version

    defaultConfig {
        applicationId = "com.gcode.gweather"
        minSdk = Version.min_sdk_version
        targetSdk = Version.target_sdk_version
        versionCode = Version.version_code
        versionName = Version.version_name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
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

    implementation(files("libs/VastTools_0.0.9_Cancey.jar"))

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