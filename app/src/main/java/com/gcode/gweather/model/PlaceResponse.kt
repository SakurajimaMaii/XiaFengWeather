package com.gcode.gweather.model

import com.gcode.gweather.R
import com.gcode.vastadapter.BaseGcodeItem

/**
 * 返回城市数据列表数据
 * @property results List<Place>
 * @constructor
 */
data class PlaceResponse(val results: List<PlaceInf>)

/**
 * 城市数据类
 * @property id String 城市ID
 * @property name String 城市名称
 * @property country String 国家代码
 * @property path String 隶属层级，从小到大
 * @property timezone String IANA标准时区名称（该名称不受夏令时影响）
 * @property timezone_offset String 相对于UTC时区的偏移量（采用夏令时的城市会因夏令时而变化）
 * @constructor
 */
class PlaceInf(
    val id: String,
    val name: String,
    val country: String,
    val path: String,
    val timezone: String,
    val timezone_offset: String
) : BaseGcodeItem {

    override fun getItemBindViewType(): Int {
        return R.layout.city_inf_recycle_item
    }

    override fun getItemViewType(): Int {
        return 0
    }
}