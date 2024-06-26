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

package com.xfw.vastgui

import android.app.Application
import android.content.Context
import android.util.Log
import com.amap.api.location.AMapLocationClient
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.style.MIUIStyle
import com.qwsdk.vastgui.QWSdk
import com.xfw.vastgui.log.mLogFactory
import com.xfw.vastgui.sp.AppSp

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // 高德地图隐私更新
        AMapLocationClient.updatePrivacyAgree(this, true)
        AMapLocationClient.updatePrivacyShow(this, true, true)
        // 配置 MIUI 主题
        DialogX.globalStyle = MIUIStyle()
        // 配置和风天气
        Log.d("App", "当前的Key为:${AppSp.key}，当前的订阅方案是:${AppSp.plan}")
        val plan = QWSdk.Plan.entries[AppSp.plan]
        val qwConfiguration = QWSdk.Configuration(plan, AppSp.key) {
            mLogFactory.getLog(QWSdk::class.java).d(it)
        }
        qw = QWSdk.getInstance(qwConfiguration)
    }

    companion object {
        lateinit var qw: QWSdk
    }

}