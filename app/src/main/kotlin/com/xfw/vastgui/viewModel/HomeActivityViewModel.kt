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

package com.xfw.vastgui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ave.vastgui.core.text.safeToDouble
import com.ave.vastgui.tools.utils.DateUtils
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartAlignType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartAnimationType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartFontWeightType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartLayoutType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartSymbolType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartVerticalAlignType
import com.github.aachartmodel.aainfographics.aachartcreator.AAOptions
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAAnimation
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAChart
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAColumn
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAItemStyle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AALabels
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AALegend
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAMarker
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAPlotOptions
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AASeries
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAStyle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AASubtitle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AATitle
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AATooltip
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAXAxis
import com.github.aachartmodel.aainfographics.aaoptionsmodel.AAYAxis
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection
import com.qwsdk.vastgui.entity.geo.lookup.GeoLookup
import com.qwsdk.vastgui.entity.historical.air.HistoricalAir
import com.qwsdk.vastgui.entity.weather.daily.WeatherDaily
import com.qwsdk.vastgui.entity.weather.now.WeatherNow
import com.qwsdk.vastgui.utils.Coordinate
import com.qwsdk.vastgui.utils.Day
import com.qwsdk.vastgui.utils.Name
import com.xfw.vastgui.App
import com.xfw.vastgui.App.Companion.qw
import com.xfw.vastgui.fragment.CityFragment
import com.xfw.vastgui.log.mLogFactory
import com.xfw.vastgui.utils.AmapUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class HomeActivityViewModel : ViewModel() {

    private val mLogger = mLogFactory.getLog(HomeActivityViewModel::class.java)

    /**
     * 待搜索的城市名。
     *
     * @see CityFragment
     */
    private val _name = MutableStateFlow("")

    /** [_location] 是请求天气等数据的核心。 */
    private val _location = MutableStateFlow("")

    // AnimatedBottomBar当前选中页面
    val currentPage = MutableLiveData<Int>()
    private val _isGpsOpen = MutableLiveData<Boolean>()
    val isGpsOpen: LiveData<Boolean>
        get() = _isGpsOpen
    private val _spin = MutableLiveData<Boolean>()
    val spin: LiveData<Boolean>
        get() = _spin

    private val _geoCityLookup: StateFlow<GeoLookup?> =
        _location.map { location ->
            if (location.isEmpty()) return@map null
            qw.geo().cityLookup(Name(location)).getOrNull()
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val geoCities =
        _name.map { name ->
            if (name.isEmpty()) return@map null
            mLogger.d("搜索城市:$name")
            qw.geo().cityLookup(Name(name))
                .onFailure { mLogger.e("搜索城市遭遇异常:${it.stackTraceToString()}") }
                .getOrNull()?.location
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val weatherNow: StateFlow<WeatherNow?> =
        _location.map { location ->
            if (location.isEmpty()) return@map null
            mLogger.d("当前要查询的天气地点是:$location")
            val coordinate = location.split(",").map { it.safeToDouble(0.0) }
            qw.weather().now(Coordinate(coordinate[0], coordinate[1])).onFailure {
                mLogger.e("当前天气查询失败:${it.stackTraceToString()}")
            }.getOrNull()
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val weather7D: StateFlow<WeatherDaily?> =
        _location.map { location ->
            if (location.isEmpty()) return@map null
            val coordinate = location.split(",").map { it.safeToDouble(0.0) }
            qw.weather().daily(Day.Day7, Coordinate(coordinate[0], coordinate[1])).onFailure {
                mLogger.e("7天天气预报查询失败", it)
            }.getOrNull()
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    private val _historicalAir: StateFlow<HistoricalAir?> =
        _geoCityLookup.map { geoBean ->
            if (null == geoBean) {
                null
            } else {
                val date = DateUtils
                    .getDayBeforeOrAfterCurrentTime("yyyyMMdd", -1)
                val location = geoBean.location[0]
                mLogger.d("空气质量,地点:${location.name},查询日期:${date}")
                val id = geoBean.location[0].getLocationID()
                qw.timeMachine().airHistory(id, date).onFailure {
                    mLogger.d("当前查询的空气质量遭遇异常:${it.stackTraceToString()}")
                }.getOrNull()
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    val chartUpdateData: StateFlow<Array<AASeriesElement>?> =
        _historicalAir.map { historicalAir ->
            if (null == historicalAir) {
                null
            } else {
                updateAirQualityData(historicalAir.airHourly)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun aqiChart(): AAOptions {
        val stopsArr: Array<Any> = arrayOf(
            arrayOf(0.2, "rgba(156,107,211,0.3)")
        )
        val gradientColorDic1: Map<String, *> = AAGradientColor.linearGradient(
            AALinearGradientDirection.ToBottom,
            stopsArr
        )
        val gradientColorDic2: Map<String, *> = AAGradientColor.linearGradient(
            AALinearGradientDirection.ToBottom,
            "#956FD4",
            "#3EACE5" //颜色字符串设置支持十六进制类型和 rgba 类型
        )
        val category = arrayOf(
            "1-1", "1-2", "1-3", "1-4", "1-5"
        )
        //pm10数据
        val pm10ValuesArr = arrayOf<Any>(
            0.0, 0.0, 0.0, 0.0, 0.0
        )
        //pm2.5数据
        val pm25ValuesArr = arrayOf<Any>(
            0.0, 0.0, 0.0, 0.0, 0.0
        )
        //空气质量指数
        val aqiValuesArr = arrayOf<Any>(
            0.0, 0.0, 0.0, 0.0, 0.0
        )
        val aaChart = AAChart()
            .backgroundColor("#152C39")
            .margin(arrayOf(130f, 70f, 50f, 70f))
        val aaTitle = AATitle()
            .text("未来五天的空气质量数据")
            .style(AAStyle().color("white"))
        val aaSubtitle = AASubtitle()
            .text("aqi与pm25数据")
            .style(AAStyle().color("white"))
        val aaLabels = AALabels()
            .enabled(true)
            .style(
                AAStyle()
                    .color("white")
            )
        val aaXAxis = AAXAxis()
            .visible(true)
            .labels(aaLabels)
            .min(0f)
            .categories(category)
        val aaYAxisTitleStyle = AAStyle()
            .color("#1e90ff") //Title font color
            .fontSize(14f) //Title font size
            .fontWeight(AAChartFontWeightType.Bold) //Title font weight
            .textOutline("0px 0px contrast")
        val yAxis1 = AAYAxis()
            .visible(true)
            .labels(aaLabels)
            .gridLineWidth(0f)
            .title(
                AATitle()
                    .text("PM10/PM2.5颗粒物预报值")
                    .style(aaYAxisTitleStyle)
            )
        val yAxis2 = AAYAxis()
            .visible(true)
            .labels(aaLabels)
            .gridLineWidth(0f)
            .title(
                AATitle()
                    .text("空气质量指数")
                    .style(aaYAxisTitleStyle)
            )
            .opposite(true)
        val aaTooltip = AATooltip()
            .enabled(true)
            .shared(true)
        val aaPlotOptions = AAPlotOptions()
            .series(
                AASeries()
                    .animation(
                        AAAnimation()
                            .easing(AAChartAnimationType.EaseTo)
                            .duration(1000)
                    )
            )
            .column(
                AAColumn()
                    .grouping(false)
                    .pointPadding(0f)
                    .pointPlacement(0f)
            )
        val aaLegend = AALegend()
            .enabled(true)
            .itemStyle(
                AAItemStyle()
                    .color("white")
            )
            .floating(true)
            .layout(AAChartLayoutType.Horizontal)
            .align(AAChartAlignType.Center)
            .x(0f)
            .verticalAlign(AAChartVerticalAlignType.Top)
            .y(40f)
        val pm25ValuesElement: AASeriesElement = AASeriesElement()
            .name("pm2.5预报值")
            .type(AAChartType.Column)
            .borderWidth(0f)
            .color(gradientColorDic2)
            .yAxis(0)
            .data(pm25ValuesArr)
        val pm10ValuesElement: AASeriesElement = AASeriesElement()
            .name("pm10预报值")
            .type(AAChartType.Column)
            .borderWidth(0f)
            .color(gradientColorDic1)
            .yAxis(0)
            .data(pm10ValuesArr)
        val aqiValuesElement = AASeriesElement()
            .name("空气质量指数")
            .type(AAChartType.Line)
            .marker(
                AAMarker()
                    .radius(7f) //曲线连接点半径，默认是4
                    .symbol(AAChartSymbolType.Circle.value) //曲线点类型："circle", "square", "diamond", "triangle","triangle-down"，默认是"circle"
                    .fillColor("#ffffff") //点的填充色(用来设置折线连接点的填充色)
                    .lineWidth(3f) //外沿线的宽度(用来设置折线连接点的轮廓描边的宽度)
                    .lineColor("") //外沿线的颜色(用来设置折线连接点的轮廓描边颜色，当值为空字符串时，默认取数据点或数据列的颜色)
            )
            .color("#F02FC2")
            .yAxis(1)
            .data(aqiValuesArr)
        return AAOptions()
            .chart(aaChart)
            .title(aaTitle)
            .subtitle(aaSubtitle)
            .xAxis(aaXAxis)
            .yAxisArray(arrayOf(yAxis1, yAxis2))
            .tooltip(aaTooltip)
            .plotOptions(aaPlotOptions)
            .legend(aaLegend)
            .touchEventEnabled(false)
            .series(
                arrayOf(
                    pm25ValuesElement,
                    pm10ValuesElement,
                    aqiValuesElement
                )
            )
    }

    /**
     * 刷新天气数据。
     *
     * @see chartUpdateData
     */
    private fun updateAirQualityData(daily: List<HistoricalAir.AirHourly>): Array<AASeriesElement> {

        val stopsArr: Array<Any> = arrayOf(
            arrayOf(0.2, "rgba(156,107,211,0.3)")
        )

        val gradientColorDic1: Map<String, *> = AAGradientColor.linearGradient(
            AALinearGradientDirection.ToBottom,
            stopsArr
        )

        val gradientColorDic2: Map<String, *> = AAGradientColor.linearGradient(
            AALinearGradientDirection.ToBottom,
            "#956FD4",
            "#3EACE5" //颜色字符串设置支持十六进制类型和 rgba 类型
        )

        return arrayOf(
            AASeriesElement()
                .name("PM2.5预报值")
                .type(AAChartType.Column)
                .borderWidth(0f)
                .color(gradientColorDic2)
                .yAxis(0)
                .data(
                    arrayOf(
                        daily[0].pm2p5.toFloat(),
                        daily[1].pm2p5.toFloat(),
                        daily[2].pm2p5.toFloat(),
                        daily[3].pm2p5.toFloat(),
                        daily[4].pm2p5.toFloat()
                    )
                ),
            AASeriesElement()
                .name("PM10预报值")
                .type(AAChartType.Column)
                .borderWidth(0f)
                .color(gradientColorDic1)
                .yAxis(0)
                .data(
                    arrayOf(
                        daily[0].pm10.toFloat(),
                        daily[1].pm10.toFloat(),
                        daily[2].pm10.toFloat(),
                        daily[3].pm10.toFloat(),
                        daily[4].pm10.toFloat()
                    )
                ),
            AASeriesElement()
                .name("空气质量指数")
                .type(AAChartType.Line)
                .marker(
                    AAMarker()
                        .radius(7f) //曲线连接点半径，默认是4
                        .symbol(AAChartSymbolType.Circle.value) //曲线点类型："circle", "square", "diamond", "triangle","triangle-down"，默认是"circle"
                        .fillColor("#ffffff") //点的填充色(用来设置折线连接点的填充色)
                        .lineWidth(3f) //外沿线的宽度(用来设置折线连接点的轮廓描边的宽度)
                        .lineColor("") //外沿线的颜色(用来设置折线连接点的轮廓描边颜色，当值为空字符串时，默认取数据点或数据列的颜色)
                )
                .color("#F02FC2")
                .yAxis(1)
                .data(
                    arrayOf(
                        daily[0].aqi.toFloat(),
                        daily[1].aqi.toFloat(),
                        daily[2].aqi.toFloat(),
                        daily[3].aqi.toFloat(),
                        daily[4].aqi.toFloat()
                    )
                )
        )
    }

    /**
     * 搜索相关的城市。
     *
     * @param location 搜索地相关的坐标，参考 [AmapUtils.getLocation] 。
     */
    fun searchPlaces(coordinate: Coordinate) {
        _location.update { coordinate.location }
    }

    /** 进行城市搜索。 */
    fun searchCities(name: String) {
        _name.update { name }
    }

    fun setCurrentPage(index: Int) {
        currentPage.postValue(index)
    }

    /**
     * 设置Gps状态
     *
     * @param gpsStatus Boolean
     */
    fun setGpsStatus(
        gpsStatus: Boolean,
    ) {
        _isGpsOpen.postValue(gpsStatus)
    }

    /**
     * 是否旋转主界面列表
     *
     * @param spinList Boolean
     */
    fun spinList(spinList: Boolean) {
        _spin.postValue(spinList)
    }

    init {
        _spin.value = true
    }
}