package com.gcode.gweather.model

import com.google.gson.annotations.SerializedName

/**
 * 返回数据示例
 * 参考textFiles文件夹下面的dataJson
 */

/**
 * 当天天气情况返回数据Json
 * @property results List<Place>
 * @constructor
 */
data class NowDataResponse(val results: List<Place>)

/**
 * 地点数据类
 * @property location Location
 * @property now Now
 * @property lastUpdate String
 * @constructor
 */
data class Place(
    val location: Location,
    val now: Now,
    @SerializedName("last_update") val lastUpdate: String
)

/**
 * 位置数据类
 * @property id String
 * @property name String
 * @property country String
 * @property path String
 * @property timeZone String
 * @property timeZoneOffset String
 * @constructor
 */
data class Location(
    val id: String,
    val name: String,
    val country: String,
    val path: String,
    val timeZone: String,
    @SerializedName("timezone_offset") val timeZoneOffset: String
)

/**
 * 当前天气数据
 * @property text String 天气
 * @property code String
 * @property temperature String 温度
 * @property feelLike String 体感
 * @property pressure String 大气压
 * @property humidity String 湿度
 * @property visibility String 能见度
 * @property windDirection String 风向
 * @property windDirectionDegree String
 * @property windSpeed String 风速
 * @property windScale String
 * @property clouds String
 * @property dewPoint String
 * @constructor
 */
data class Now(
    val text: String,
    val code: String,
    val temperature: String,
    @SerializedName("feels_like") val feelLike: String,
    val pressure: String,
    val humidity: String,
    val visibility: String,
    @SerializedName("wind_direction") val windDirection: String,
    @SerializedName("wind_direction_degree") val windDirectionDegree: String,
    @SerializedName("wind_speed") val windSpeed: String,
    @SerializedName("wind_scale") val windScale: String,
    val clouds: String,
    @SerializedName("dew_point") val dewPoint: String
)