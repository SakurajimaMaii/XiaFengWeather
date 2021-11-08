package com.gcode.gweather.model

import com.gcode.gweather.R
import com.gcode.gweather.utils.AppUtils
import com.gcode.vastadapter.BaseGcodeItem

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
    val humidity: String
) : BaseGcodeItem {

    private val icon: String = when (text_day) {
        "晴" ->
            AppUtils.context.resources.getString(R.string.ic_sunny_day)
        "多云" ->
            AppUtils.context.resources.getString(R.string.ic_partly_cloudy)
        "阵雨" ->
            AppUtils.context.resources.getString(R.string.ic_shower)
        "阴" ->
            AppUtils.context.resources.getString(R.string.ic_cloudy_day)
        "雾" ->
            AppUtils.context.resources.getString(R.string.ic_fog)
        else -> AppUtils.context.resources.getString(R.string.ic_sunny_day)
    }

    fun getIcon(): String {
        return icon
    }

    override fun getItemBindViewType(): Int {
        return R.layout.daily_weather_recycle_item
    }

    override fun getItemViewType(): Int {
        return 0
    }
}