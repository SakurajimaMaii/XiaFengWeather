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

/**
 * 城市数据类
 * @property id String 城市ID
 * @property name String 城市名称
 * @property country String 国家代码
 * @property path String 隶属层级，从小到大
 * @property timezone_offset String 相对于UTC时区的偏移量（采用夏令时的城市会因夏令时而变化）
 * @constructor
 */
class PlaceInf(
    val id: String,
    val name: String,
    val country: String,
    val path: String,
    val timezone_offset: String,
    override var vbAapClickEventListener: VAapClickEventListener? = null,
    override var vbAdpLongClickEventListener: VAdpLongClickEventListener? = null
) : VastBindAdapterItem {

    override fun getVBAdpItemType(): Int {
        return R.layout.item_city_inf_recycle
    }

}