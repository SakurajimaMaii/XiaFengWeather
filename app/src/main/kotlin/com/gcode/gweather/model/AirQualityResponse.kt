package com.gcode.gweather.model

import com.google.gson.annotations.SerializedName

data class AQResponse(val results:List<AQPlace>)

//https://docs.seniverse.com/api/air/daily5d.html
/**
 * 请求回的地区数据集
 * 详情参考上方链接
 * @property location AQLoction 地址数据
 * @property daily List<AQDailyAirQuality> 指定days天数的空气质量
 * @property lastUpdate String 预报发布时间
 * @constructor
 */
data class AQPlace(val location: AQLoction,
                   val daily:List<AQDailyAirQuality>,
                   @SerializedName("last_update") val lastUpdate:String)

/**
 * 地址数据详情内容
 * @property id String
 * @property name String 示例 北京
 * @property country String 示例 CN
 * @property path String 示例 北京,北京,中国
 * @property timezone String 示例 Asia/Shanghai
 * @property timezoneOffset String 示例 +08:00
 * @constructor
 */
data class AQLoction(val id:String,
                     val name:String,
                     val country:String,
                     val path:String,
                     val timezone:String,
                     @SerializedName("timezone_offset")val timezoneOffset:String)

/**
 * 空气质量详情数据
 * @property aqi String 空气质量指数(AQI)是描述空气质量状况的定量指数
 * @property pm25 String PM2.5颗粒物（粒径小于等于2.5μm）预报值。单位：μg/m³
 * @property pm10 String PM10颗粒物（粒径小于等于10μm）预报值。单位：μg/m³
 * @property so2 String 二氧化硫预报值。单位：μg/m³
 * @property no2 String 二氧化氮预报值。单位：μg/m³
 * @property co String 一氧化碳预报值。单位：mg/m³
 * @property o3 String 臭氧预报值。单位：μg/m³
 * @property quality String 空气质量类别，有“优、良、轻度污染、中度污染、重度污染、严重污染”6类
 * @property date String 预报日期
 * @constructor
 */
data class AQDailyAirQuality(val aqi:String,
                             val pm25:String,
                             val pm10:String,
                             val so2:String,
                             val no2:String,
                             val co:String,
                             val o3:String,
                             val quality:String,
                             val date:String)