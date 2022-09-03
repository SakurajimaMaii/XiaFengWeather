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

package com.gcode.gweather

import android.app.Application
import com.amap.api.maps.MapsInitializer
import com.gcode.vasttools.ToolsConfig
import com.qweather.sdk.view.HeConfig

class App : Application() {

    companion object{
        const val TOKEN = "95f1a215ad5d4eaa828a99f6ebda710c"
    }

    override fun onCreate() {
        super.onCreate()
        ToolsConfig.init(this)
        // 高德地图隐私更新
        MapsInitializer.updatePrivacyAgree(this, true)
        MapsInitializer.updatePrivacyShow(this, true, true)
        // 和风天气更新
        HeConfig.init("", "")
        HeConfig.switchToBizService()
    }

}