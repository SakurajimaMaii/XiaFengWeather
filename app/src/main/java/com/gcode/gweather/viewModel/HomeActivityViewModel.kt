package com.gcode.gweather.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.gcode.gweather.model.AQDailyAirQuality
import com.gcode.gweather.model.PlaceInf
import com.gcode.gweather.network.Repository
import com.gcode.vasttools.utils.LogUtils
import com.github.aachartmodel.aainfographics.aachartcreator.*
import com.github.aachartmodel.aainfographics.aaoptionsmodel.*
import com.github.aachartmodel.aainfographics.aatools.AAGradientColor
import com.github.aachartmodel.aainfographics.aatools.AALinearGradientDirection

class HomeActivityViewModel : ViewModel() {

    companion object {
        //请求天数
        private const val DAYS: Int = 5
    }

    //chartModel 刷新数据
    private val _chartModelUpdateSeriesArray = MutableLiveData<Array<AASeriesElement>>()

    val chartModelUpdateSeriesArray: LiveData<Array<AASeriesElement>>
        get() = _chartModelUpdateSeriesArray

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
     * 刷新天气数据
     * @param daily MutableList<AQDailyAirQuality>
     */
    fun updateAirQualityData(daily: List<AQDailyAirQuality>) {

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

        _chartModelUpdateSeriesArray.postValue(
            arrayOf(
                AASeriesElement()
                    .name("PM2.5预报值")
                    .type(AAChartType.Column)
                    .borderWidth(0f)
                    .color(gradientColorDic2)
                    .yAxis(0)
                    .data(
                        arrayOf(
                            daily[0].pm25.toFloat(),
                            daily[1].pm25.toFloat(),
                            daily[2].pm25.toFloat(),
                            daily[3].pm25.toFloat(),
                            daily[4].pm25.toFloat()
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
        )
    }

    /**
     * location是请求天气等数据的核心
     */
    private val _location = MutableLiveData<String>()

    private val _temperature = MutableLiveData<Float>()
    private val _feelslike = MutableLiveData<Int>()
    private val _humidity = MutableLiveData<Float>()
    private val _windDirection = MutableLiveData<String>()
    private val _windSpeed = MutableLiveData<Float>()
    private val _weather = MutableLiveData<String>()
    private val _visibility = MutableLiveData<Float>()
    private val _aqDailyDataList = MutableLiveData<List<AQDailyAirQuality>>() //天气数据

    /**
     * 只暴露不可变LiveData给外部
     */
    val temperature: LiveData<Float>
        get() = _temperature

    val feelslike: LiveData<Int>
        get() = _feelslike

    val humidity: LiveData<Float>
        get() = _humidity

    val windSpeed: LiveData<Float>
        get() = _windSpeed

    val weather: LiveData<String>
        get() = _weather

    val visibility: LiveData<Float>
        get() = _visibility

    /**
     * 初始化
     */
    init {
        _temperature.value = 0f
        _feelslike.value = 0
        _humidity.value = 0f
        _windDirection.value = ""
        _windSpeed.value = 0f
        _weather.value = ""
        _visibility.value = 0f
        _aqDailyDataList.value = ArrayList()
    }

    /**
     * 刷新天气数据
     * @param temperatureValue Float 温度，单位为c摄氏度或f华氏度
     * @param feelslikeValue Int 体感温度，单位为c摄氏度或f华氏度
     * @param humidityValue Float 相对湿度，0~100，单位为百分比
     * @param windSpeedValue Float 风速，单位为km/h公里每小时或mph英里每小时
     * @param weather String
     * @param visibility Float 能见度，单位为km公里或mi英里
     */
    fun updateWeatherData(
        temperatureValue: Float,
        feelslikeValue: Int,
        humidityValue: Float,
        windDirectionValue: String,
        windSpeedValue: Float,
        weather: String,
        visibility: Float
    ) {
        /**
         * postValue保证在任何线程都可以调用
         */
        _temperature.postValue(temperatureValue)
        _feelslike.postValue(feelslikeValue)
        _humidity.postValue(humidityValue)
        _windDirection.postValue(windDirectionValue)
        _windSpeed.postValue(windSpeedValue)
        _weather.postValue(weather)
        _visibility.postValue(visibility)
    }

    val placeLiveData = Transformations.switchMap(_location) { location ->
        Repository.searchPlaceWeather(location)
    }

    val dailyAirQualityResult = Transformations.switchMap(_location) { location ->
        Repository.searchAirQuality(location, DAYS)
    }

    val dailyWeatherResult = Transformations.switchMap(_location) { location ->
        Repository.searchDailyWeather(location)
    }

    fun searchPlaces(location: String) {
        _location.value = location
        LogUtils.i(this.javaClass,"HomeActivityViewModel", "当前查询的location是$location")
    }

    //AnimatedBottomBar当前选中页面
    val currentPage = MutableLiveData<Int>()

    fun setCurrentPage(index: Int) {
        currentPage.postValue(index)
    }

    private val cityCode = MutableLiveData<String>()

    val cityListJson = Transformations.switchMap(cityCode) { city ->
        Repository.searchPlace(city)
    }

    /**
     * 根据Code搜索城市
     * @param cityCode String
     */
    fun searchCity(
        cityCode: String,
    ) {
        this.cityCode.postValue(cityCode)
    }

    private val _isGpsOpen = MutableLiveData<Boolean>()

    val isGpsOpen: LiveData<Boolean>
        get() = _isGpsOpen

    /**
     * 设置Gps状态
     * @param gpsStatus Boolean
     */
    fun setGpsStatus(
        gpsStatus: Boolean,
    ) {
        _isGpsOpen.postValue(gpsStatus)
    }

    private val _placeInf = MutableLiveData<PlaceInf>()

    val placeInf: LiveData<PlaceInf>
        get() = _placeInf

    /**
     * 更新PlaceInf
     * @param placeInf PlaceInf
     */
    fun setPlaceInf(placeInf: PlaceInf) {
        _placeInf.postValue(placeInf)
        _location.postValue(placeInf.id)
    }

    private val _spin = MutableLiveData<Boolean>()

    val spin: LiveData<Boolean>
        get() = _spin

    /**
     * 是否旋转主界面列表
     * @param spinList Boolean
     */
    fun spinList(spinList: Boolean) {
        _spin.postValue(spinList)
    }

    init {
        _spin.value = true
    }
}