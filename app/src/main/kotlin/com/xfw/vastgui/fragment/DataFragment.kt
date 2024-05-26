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

package com.xfw.vastgui.fragment

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.ave.vastgui.tools.fragment.VastVbVmFragment
import com.ave.vastgui.tools.utils.ScreenSizeUtils.getMobileScreenHeight
import com.ave.vastgui.tools.utils.ScreenSizeUtils.getMobileScreenWidth
import com.ave.vastgui.tools.utils.findByContext
import com.qwsdk.vastgui.utils.Coordinate
import com.xfw.vastgui.R
import com.xfw.vastgui.databinding.FragmentDataBinding
import com.xfw.vastgui.utils.AmapUtils
import com.xfw.vastgui.viewModel.HomeActivityViewModel
import com.scwang.smart.refresh.header.BezierRadarHeader
import kotlinx.coroutines.launch

class DataFragment : VastVbVmFragment<FragmentDataBinding, HomeActivityViewModel>() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 初始化界面
        initUI()
        initClickListener()
        // 更新数据
        lifecycleScope.launch {
            getViewModel().weatherNow.collect { weatherNowBean ->
                if (null == weatherNowBean) return@collect
                getBinding().temperatureValue.text = weatherNowBean.now?.let {
                    String.format(
                        findByContext { getString(R.string.df_temperature) },
                        it.temp
                    )
                }

                getBinding().feelsLikeValue.text = weatherNowBean.now?.let {
                    String.format(
                        findByContext { getString(R.string.df_feelslike_temperature) },
                        it.feelsLike
                    )
                }

                getBinding().visibilityValue.text = weatherNowBean.now?.let {
                    String.format(
                        findByContext { getString(R.string.df_visibility) },
                        it.vis
                    )
                }

                getBinding().humidityValue.text = weatherNowBean.now?.let {
                    String.format(
                        findByContext { getString(R.string.idw_daily_humidity) },
                        it.humidity
                    )
                }

                getBinding().windSpeedValue.text = weatherNowBean.now?.let {
                    String.format(
                        findByContext { getString(R.string.df_wind_speed) },
                        it.windSpeed
                    )
                }

                when (weatherNowBean.now?.text) {
                    "晴" -> {
                        getBinding().apply {
                            weatherValue.text =
                                String.format(
                                    findByContext { getString(R.string.df_en_hans_weather) },
                                    "sun",
                                    weatherNowBean.now!!.text
                                )
                            weatherIcon.setImageResource(R.drawable.sun)
                        }
                    }

                    "多云" -> {
                        getBinding().apply {
                            weatherValue.text =
                                String.format(
                                    findByContext { getString(R.string.df_en_hans_weather) },
                                    "cloudy",
                                    weatherNowBean.now!!.text
                                )
                            weatherIcon.setImageResource(R.drawable.partly_cloudy)
                        }
                    }

                    "阵雨" -> {
                        getBinding().apply {
                            weatherValue.text =
                                String.format(
                                    findByContext { getString(R.string.df_en_hans_weather) },
                                    "showers",
                                    weatherNowBean.now!!.text
                                )
                            weatherIcon.setImageResource(R.drawable.light_rain)
                        }
                    }

                    "阴" -> {
                        getBinding().apply {
                            weatherValue.text =
                                String.format(
                                    findByContext { getString(R.string.df_en_hans_weather) },
                                    "cloudy",
                                    weatherNowBean.now!!.text
                                )
                            weatherIcon.setImageResource(R.drawable.partly_cloudy)
                        }
                    }
                }
            }
        }

        lifecycleScope.launch {
            getViewModel().chartUpdateData.collect {
                if (null == it) return@collect
                getBinding().airQualityChartView.aa_onlyRefreshTheChartDataWithChartOptionsSeriesArray(
                    it, true
                )
            }
        }
    }

    private fun initClickListener() {
        getBinding().apply {
            refreshLayout.setOnLoadMoreListener { refreshLayout ->
                var location: Coordinate
                lifecycleScope.launch {
                    location = AmapUtils.getLocation()
                    getViewModel().searchPlaces(location)
                }
                refreshLayout.finishLoadMore(1500 /*,false*/) //传入false表示加载失败
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initUI() {
        getBinding().apply {
            airQualityChartView.aa_drawChartWithChartOptions(getViewModel().aqiChart())

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
                    findByContext { getString(R.string.df_feelslike_temperature) },
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