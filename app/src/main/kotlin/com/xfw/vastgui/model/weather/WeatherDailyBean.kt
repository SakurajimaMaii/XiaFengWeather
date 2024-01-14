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

package com.xfw.vastgui.model.weather

import com.ave.vastgui.tools.utils.findByContext
import com.xfw.vastgui.R
import com.qweather.sdk.bean.weather.WeatherDailyBean

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2024/1/14 15:29

fun WeatherDailyBean.DailyBean.icon(): String = when (iconDay) {
    "晴" ->
        findByContext { getString(R.string.ic_sunny_day) }

    "多云" ->
        findByContext { getString(R.string.ic_partly_cloudy) }

    "阵雨" ->
        findByContext { getString(R.string.ic_shower) }

    "阴" ->
        findByContext { getString(R.string.ic_cloudy_day) }

    "雾" ->
        findByContext { getString(R.string.ic_fog) }

    else -> findByContext { getString(R.string.ic_sunny_day) }
}