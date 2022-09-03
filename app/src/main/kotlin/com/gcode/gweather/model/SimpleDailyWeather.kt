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

package com.gcode.gweather.model

import com.gcode.gweather.R
import com.gcode.vastadapter.interfaces.VAapClickEventListener
import com.gcode.vastadapter.interfaces.VAdpLongClickEventListener
import com.gcode.vastadapter.interfaces.VastBindAdapterItem
import com.gcode.vasttools.utils.ResUtils

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2021/3/11 20:15
// Description:
// Documentation:

class SimpleDailyWeather(
    val date: String,
    text_day: String,
    val high: String,
    val low: String,
    val windSpeed: String,
    val humidity: String,
    override var vbAapClickEventListener: VAapClickEventListener? = null,
    override var vbAdpLongClickEventListener: VAdpLongClickEventListener? = null
) : VastBindAdapterItem {

    private val icon: String = when (text_day) {
        "晴" ->
            ResUtils.getString(R.string.ic_sunny_day)
        "多云" ->
            ResUtils.getString(R.string.ic_partly_cloudy)
        "阵雨" ->
            ResUtils.getString(R.string.ic_shower)
        "阴" ->
            ResUtils.getString(R.string.ic_cloudy_day)
        "雾" ->
            ResUtils.getString(R.string.ic_fog)
        else -> ResUtils.getString(R.string.ic_sunny_day)
    }

    fun getIcon(): String {
        return icon
    }

    override fun getVBAdpItemType(): Int {
        return R.layout.daily_weather_recycle_item
    }


}