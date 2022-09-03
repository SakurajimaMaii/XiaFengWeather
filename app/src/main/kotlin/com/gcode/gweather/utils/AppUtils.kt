package com.gcode.gweather.utils

import android.app.Application
import com.gcode.vasttools.ToolsConfig
import com.gcode.vasttools.utils.ScreenSizeUtils

/**
 * 获取全局Context
 */
class AppUtils : Application() {
    companion object {
        //心知天气私钥
        const val TOKEN = ""
    }

    override fun onCreate() {
        super.onCreate()
        ToolsConfig.init(this)
    }
}