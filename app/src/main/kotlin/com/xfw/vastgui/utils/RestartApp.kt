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

package com.xfw.vastgui.utils

import android.content.ComponentName
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.ave.vastgui.tools.content.ContextHelper
import com.ave.vastgui.tools.utils.AppUtils
import com.loc.an
import kotlin.jvm.Throws
import kotlin.system.exitProcess

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2024/5/27 22:13
// Description: 用来重启应用

/**
 * 重启应用
 * @param isKillProcess 是否杀死本进程
 */
@Throws(RuntimeException::class)
fun relaunchApp(isKillProcess: Boolean) {
    val intent = getLaunchAppIntent(AppUtils.getPackageName(), true)
        ?: throw RuntimeException("Didn't exist launcher activity.")
    intent.addFlags(
        Intent.FLAG_ACTIVITY_NEW_TASK
                or Intent.FLAG_ACTIVITY_CLEAR_TOP
                or Intent.FLAG_ACTIVITY_CLEAR_TASK
    )
    ContextHelper.getAppContext().startActivity(intent)
    if (!isKillProcess) return
    android.os.Process.killProcess(android.os.Process.myPid())
    exitProcess(0)
}

/**
 * 获取一个包名的启动intent
 */
fun getLaunchAppIntent(packageName: String, isNewTask: Boolean): Intent? {
    val launcherActivity = getLauncherActivity(packageName)
    if (launcherActivity.isNotEmpty()) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val cn = ComponentName(packageName, launcherActivity)
        intent.component = cn
        return if (isNewTask) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) else intent
    }
    return null
}

/**
 * 根据包名来获取其启动项app
 */
fun getLauncherActivity(pkg: String): String {
    val intent = Intent(Intent.ACTION_MAIN, null)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    intent.setPackage(pkg)
    val pm = ContextHelper.getAppContext().packageManager
    val info = pm.queryIntentActivities(intent, 0)
    val size = info.size
    if (size == 0) return ""
    for (i in 0 until size) {
        val ri = info[i]
        if (ri.activityInfo.processName == pkg) {
            return ri.activityInfo.name
        }
    }
    return info[0].activityInfo.name
}