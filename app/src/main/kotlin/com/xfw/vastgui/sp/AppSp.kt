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

package com.xfw.vastgui.sp

import android.content.SharedPreferences
import com.ave.vastgui.tools.sharedpreferences.ISharedPreferencesOwner
import com.ave.vastgui.tools.sharedpreferences.SpNormal
import com.ave.vastgui.tools.sharedpreferences.int
import com.ave.vastgui.tools.sharedpreferences.string
import com.qwsdk.vastgui.QWSdk

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2024/5/27 23:16
// Description: 用来存储 App 相关变量

object AppSp : ISharedPreferencesOwner {
    override val name: String = "AppSp"
    override val kv: SharedPreferences =
        SpNormal.getInstance(name).getSharedPreferences()

    /** 用来存储当前选择的方案 */
    var plan by int(QWSdk.Plan.Free.ordinal)

    /** 用来存储和风天气的key */
    var key by string("")
}