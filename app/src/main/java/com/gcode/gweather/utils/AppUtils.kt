package com.gcode.gweather.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Typeface
import com.gcode.vasttools.utils.ScreenSizeUtils

/**
 * 获取全局Context
 */
class AppUtils : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        //心知天气私钥
        const val TOKEN = ""
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}

/**
 * 用于在设计尺寸时起作用
 * @receiver ScreenSizeUtils
 * @return Float
 */
fun ScreenSizeUtils.getDensity() = AppUtils.context.resources.displayMetrics.density