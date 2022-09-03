package com.gcode.gweather.model

import com.google.gson.annotations.SerializedName

/**
 *作者:created by HP on 2021/3/11 11:20
 *邮箱:sakurajimamai2020@qq.com
 */
data class DailyDataResponse(val results: List<WeatherDaily>)

/**
 * 地点数据类
 * @property location Location
 * @property daily List<DailyWeather>
 * @property lastUpdate String
 * @constructor
 */
data class WeatherDaily(
    val location: Location,
    val daily: List<DailyWeather>,
    @SerializedName("last_update") val lastUpdate: String
)

/**
 *
 * @property date String 日期
 * @property text_day String 白天天气现象文字
 * @property code_day String 白天天气现象代码
 * @property text_night String 晚间天气现象文字
 * @property code_night String 晚间天气现象代码
 * @property high String 当天最高温度
 * @property low String 当天最低温度
 * @property rainfall String 降水概率，范围0~100，单位百分比（目前仅支持国外城市）
 * @property precip String 风向文字
 * @property windDirection String 风向角度，范围0~360
 * @property windDirectionDegree String 风速，单位km/h（当unit=c时）、mph（当unit=f时）
 * @property windSpeed String 风力等级
 * @property windScale String 降水量，单位mm
 * @property humidity String 相对湿度，0~100，单位为百分比
 * @constructor
 */
data class DailyWeather(
    val date: String,
    val text_day: String,
    val code_day: String,
    val text_night: String,
    val code_night: String,
    val high: String,
    val low: String,
    private val rainfall: String,
    private val precip: String,
    @SerializedName("wind_direction") val windDirection: String,
    @SerializedName("wind_direction_degree") val windDirectionDegree: String,
    @SerializedName("wind_speed") val windSpeed: String,
    @SerializedName("wind_scale") val windScale: String,
    val humidity: String
)