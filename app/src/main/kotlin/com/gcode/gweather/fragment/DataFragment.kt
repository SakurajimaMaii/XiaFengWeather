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

package com.gcode.gweather.fragment

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.gcode.gweather.R
import com.gcode.gweather.databinding.FragmentDataBinding
import com.gcode.gweather.utils.AmapUtils
import com.gcode.gweather.viewModel.HomeActivityViewModel
import com.gcode.vasttools.fragment.VastVbVmFragment
import com.gcode.vasttools.utils.ResUtils
import com.gcode.vasttools.utils.ScreenSizeUtils.getMobileScreenHeight
import com.gcode.vasttools.utils.ScreenSizeUtils.getMobileScreenWidth
import com.qweather.sdk.bean.history.HistoricalAirBean
import com.qweather.sdk.bean.weather.WeatherNowBean
import com.qweather.sdk.view.QWeather
import com.scwang.smart.refresh.header.BezierRadarHeader
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DataFragment : VastVbVmFragment<FragmentDataBinding, HomeActivityViewModel>() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun initView(view: View, savedInstanceState: Bundle?) {
        // 初始化界面
        initUI()
        initClickListener()
        // 更新数据
        mViewModel.apply {
            location.observe(requireActivity()) { location ->
                QWeather.getWeatherNow(
                    requireActivity(),
                    location,
                    object : QWeather.OnResultWeatherNowListener {
                        override fun onError(p0: Throwable?) {
                            p0?.printStackTrace()
                        }

                        override fun onSuccess(weatherNowBean: WeatherNowBean?) {
                            val nowWeather = weatherNowBean?.now
                            if (nowWeather != null) {
                                mViewModel.updateWeatherData(
                                    nowWeather.temp.toFloat(),
                                    nowWeather.feelsLike.toInt(),
                                    nowWeather.humidity.toFloat(),
                                    nowWeather.windDir,
                                    nowWeather.windSpeed.toFloat(),
                                    nowWeather.text,
                                    nowWeather.vis.toFloat()
                                )
                            }
                        }
                    })
            }

            locationID.observe(requireActivity()) {
                val sdf = SimpleDateFormat("yyyyMMdd")
                var date = Date()
                val calendar: Calendar = Calendar.getInstance()
                calendar.time = date
                calendar.add(Calendar.DAY_OF_MONTH, -1)
                date = calendar.time
                QWeather.getHistoricalAir(
                    requireActivity(),
                    it,
                    sdf.format(date),
                    object : QWeather.OnResultAirHistoricalBeanListener {
                        override fun onError(p0: Throwable?) {
                            p0?.printStackTrace()
                        }

                        override fun onSuccess(historicalAirBean: HistoricalAirBean?) {
                            val dailyBeans = historicalAirBean?.airHourlyBeans
                            if (null != dailyBeans) {
                                mViewModel.updateAirQualityData(dailyBeans)
                            }
                        }
                    }
                )
            }

            temperature.observe(viewLifecycleOwner) { temperatureValue ->
                mBinding.temperatureValue.text = String.format(
                    ResUtils.getString(R.string.temperature_value),
                    temperatureValue
                )
            }

            mViewModel.feelslike.observe(viewLifecycleOwner) { feelslike ->
                mBinding.feelsLikeValue.text = String.format(
                    ResUtils.getString(R.string.feelslike_temperature_value),
                    feelslike
                )
            }

            visibility.observe(viewLifecycleOwner) { visibilityValue ->
                mBinding.visibilityValue.text = String.format(
                    ResUtils.getString(R.string.visibility_value),
                    visibilityValue
                )
            }

            humidity.observe(viewLifecycleOwner) { humidityValue ->
                mBinding.humidityValue.text = String.format(
                    ResUtils.getString(R.string.daily_humidity),
                    humidityValue
                )
            }

            windSpeed.observe(viewLifecycleOwner) { windSpeedValue ->
                mBinding.windSpeedValue.text = String.format(
                    ResUtils.getString(R.string.wind_speed_value),
                    windSpeedValue
                )
            }

            // 更新天气文字描述
            weather.observe(viewLifecycleOwner) { weather ->
                when (weather) {
                    "晴" -> {
                        mBinding.apply {
                            weatherValue.text =
                                String.format(
                                    ResUtils.getString(R.string.en_hans_weather_value),
                                    "sun",
                                    weather
                                )
                            weatherIcon.setImageResource(R.drawable.sun)
                        }
                    }
                    "多云" -> {
                        mBinding.apply {
                            weatherValue.text =
                                String.format(
                                    ResUtils.getString(R.string.en_hans_weather_value),
                                    "cloudy",
                                    weather
                                )
                            weatherIcon.setImageResource(R.drawable.partly_cloudy)
                        }
                    }
                    "阵雨" -> {
                        mBinding.apply {
                            weatherValue.text =
                                String.format(
                                    ResUtils.getString(R.string.en_hans_weather_value),
                                    "showers",
                                    weather
                                )
                            weatherIcon.setImageResource(R.drawable.light_rain)
                        }
                    }
                    "阴" -> {
                        mBinding.apply {
                            weatherValue.text =
                                String.format(
                                    ResUtils.getString(R.string.en_hans_weather_value),
                                    "cloudy",
                                    weather
                                )
                            weatherIcon.setImageResource(R.drawable.partly_cloudy)
                        }
                    }
                }
            }

            chartModelUpdateSeriesArray.observe(requireActivity()) { seriesArray ->
                mBinding.airQualityChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                    seriesArray, true
                )
            }
        }
    }

    private fun initClickListener() {
        mBinding.apply {
            refreshLayout.setOnLoadMoreListener { refreshLayout ->
                var location: String
                lifecycleScope.launch {
                    location = AmapUtils.getLocation()
                    mViewModel.searchPlaces(location)
                }
                refreshLayout.finishLoadMore(1500 /*,false*/) //传入false表示加载失败
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initUI() {
        mBinding.apply {
            airQualityChartView.aa_drawChartWithChartOptions(mViewModel.aqiChart())

            //获取屏幕高度
            val screenHeight = getMobileScreenHeight()
            val screenWidth = getMobileScreenWidth()

            firstDataCardView.minimumHeight = screenHeight / 20

            //设置温度字体样式
            temperatureValue.apply {
                textSize = screenWidth / 20.toFloat()
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
            weatherIcon.setImageResource(R.drawable.sun) //默认晴天
            //设置天气字体大小
            weatherValue.apply {
                textSize = screenWidth / 50.toFloat()
                weatherValue.text = String.format(
                    ResUtils.getString(R.string.feelslike_temperature_value),
                    "sun",
                    "晴"
                )
            }
            //设置体表温度字体样式
            feelsLikeValue.textSize = screenWidth / 60.toFloat()
            //设置能见度字体样式
            visibilityValue.apply {
                textSize = screenWidth / 50.toFloat()
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
            //设置湿度字体样式
            humidityValue.apply {
                textSize = screenWidth / 50.toFloat()
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }
            windSpeedValue.apply {
                textSize = screenWidth / 50.toFloat()
                typeface = Typeface.defaultFromStyle(Typeface.BOLD)
            }

            //设置 Header 为 贝塞尔雷达 样式
            refreshLayout.setEnableLoadMore(false)
            //设置 Footer 为 球脉冲 样式
            refreshLayout.setRefreshHeader(
                BezierRadarHeader(requireActivity()).setEnableHorizontalDrag(true)
            )
        }
    }
}