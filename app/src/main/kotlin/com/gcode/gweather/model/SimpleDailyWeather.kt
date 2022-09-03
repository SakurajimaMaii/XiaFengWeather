package com.gcode.gweather.model

import com.gcode.gweather.R
import com.gcode.vastadapter.interfaces.VAapClickEventListener
import com.gcode.vastadapter.interfaces.VAdpLongClickEventListener
import com.gcode.vastadapter.interfaces.VastBindAdapterItem
import com.gcode.vasttools.utils.ResUtils

/**
 *作者:created by HP on 2021/3/11 20:15
 *邮箱:sakurajimamai2020@qq.com
 */
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