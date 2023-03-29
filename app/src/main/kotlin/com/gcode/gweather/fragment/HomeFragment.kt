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

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ave.vastgui.tools.fragment.VastVbVmFragment
import com.ave.vastgui.tools.utils.ToastUtils
import com.gcode.gweather.BR
import com.gcode.gweather.R
import com.gcode.gweather.databinding.FragmentHomeBinding
import com.gcode.gweather.model.SimpleDailyWeather
import com.gcode.gweather.viewModel.HomeActivityViewModel
import com.gcode.vastadapter.base.VastBindAdapter
import com.gcode.vastadapter.interfaces.VastBindAdapterItem
import com.qweather.sdk.bean.weather.WeatherDailyBean
import com.qweather.sdk.view.QWeather

class HomeFragment : VastVbVmFragment<FragmentHomeBinding, HomeActivityViewModel>() {

    private class DataBindingAdapter(
        items: MutableList<VastBindAdapterItem>,
        mContext: Context
    ) : VastBindAdapter(items,mContext) {
        override fun setVariableId(): Int {
            return BR.item
        }
    }

    private val dailyWeatherList: MutableList<VastBindAdapterItem> = ArrayList()
    private lateinit var adapter:DataBindingAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DataBindingAdapter(dailyWeatherList,requireActivity())
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        val bundle = arguments
        val username = bundle?.getString("username")
        ToastUtils.showShortMsg(username.toString())

        getBinding().dailyWeatherList.adapter = adapter
        getBinding().dailyWeatherList.layoutManager = layoutManager

        getViewModel().apply {
            /**
             * 更新温度文字描述
             */
            temperature.observe(viewLifecycleOwner) { temperatureValue ->
                getBinding().temperatureValue.text = "$temperatureValue°"
            }

            /**
             * 更新天气文字描述
             */
            weather.observe(viewLifecycleOwner) { weather ->
                when (weather) {
                    "晴" -> {
                        getBinding().apply {
                            weatherValue.text = "sun $weather"
                            weatherIcon.setImageResource(R.drawable.sun)
                        }
                    }
                    "多云" -> {
                        getBinding().apply {
                            weatherValue.text = "cloudy $weather"
                            weatherIcon.setImageResource(R.drawable.partly_cloudy)
                        }
                    }
                    "阵雨" -> {
                        getBinding().apply {
                            weatherValue.text = "showers $weather"
                            weatherIcon.setImageResource(R.drawable.light_rain)
                        }
                    }
                    "阴" -> {
                        getBinding().apply {
                            weatherValue.text = "cloudy $weather"
                            weatherIcon.setImageResource(R.drawable.partly_cloudy)
                        }
                    }
                }
            }

            placeInf.observe(viewLifecycleOwner) {
                getBinding().cityName.text = it.name
            }

            location.observe(viewLifecycleOwner) { location ->
                QWeather.getWeather10D(requireActivity() ,location,object :QWeather.OnResultWeatherDailyListener{
                    override fun onError(throwable: Throwable?) {
                        throwable?.printStackTrace()
                    }

                    override fun onSuccess(weatherDailyBean: WeatherDailyBean?) {
                        val daily = weatherDailyBean?.daily
                        if(null != daily){
                            getViewModel().updateDailyWeathers(daily)
                        }
                    }
                })
            }

            dailyWeathers.observe(requireActivity()){dailyWeathers->
                dailyWeatherList.clear()
                    for (item in dailyWeathers) {
                        dailyWeatherList.add(
                            SimpleDailyWeather(
                                item.fxDate,
                                item.textDay,
                                item.tempMax,
                                item.tempMin,
                                item.windSpeedDay,
                                item.humidity
                            )
                        )
                    }
                    adapter.notifyDataSetChanged()
            }

            isGpsOpen.observe(viewLifecycleOwner) {
                if (it) {
                    getBinding().dailyWeatherList.visibility = View.VISIBLE
                    getBinding().progressBar.visibility = View.GONE
                } else {
                    getBinding().dailyWeatherList.visibility = View.GONE
                    getBinding().progressBar.visibility = View.VISIBLE
                }
            }

            spin.observe(viewLifecycleOwner) {
                layoutManager = when (it) {
                    true ->
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    false ->
                        LinearLayoutManager(requireActivity())
                }
                getBinding().dailyWeatherList.layoutManager = layoutManager
            }
        }
    }

}