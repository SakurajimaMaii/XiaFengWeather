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
import com.gcode.gweather.BR
import com.gcode.gweather.R
import com.gcode.gweather.databinding.HomeFragmentBinding
import com.gcode.gweather.model.SimpleDailyWeather
import com.gcode.gweather.viewModel.HomeActivityViewModel
import com.gcode.vastadapter.base.VastBindAdapter
import com.gcode.vastadapter.interfaces.VastBindAdapterItem
import com.gcode.vasttools.fragment.VastVbVmFragment
import com.gcode.vasttools.utils.ToastUtils
import com.qweather.sdk.bean.weather.WeatherDailyBean
import com.qweather.sdk.view.QWeather

class HomeFragment : VastVbVmFragment<HomeFragmentBinding,HomeActivityViewModel>() {

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
    override fun initView(view: View, savedInstanceState: Bundle?) {

        adapter = DataBindingAdapter(dailyWeatherList,requireActivity())
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        val bundle = arguments
        val username = bundle?.getString("username")
        ToastUtils.showShortMsg(requireActivity(), username.toString())

        mBinding.dailyWeatherList.adapter = adapter
        mBinding.dailyWeatherList.layoutManager = layoutManager

        mViewModel.apply {
            /**
             * 更新温度文字描述
             */
            temperature.observe(viewLifecycleOwner) { temperatureValue ->
                mBinding.temperatureValue.text = "$temperatureValue°"
            }

            /**
             * 更新天气文字描述
             */
            weather.observe(viewLifecycleOwner) { weather ->
                when (weather) {
                    "晴" -> {
                        mBinding.apply {
                            weatherValue.text = "sun $weather"
                            weatherIcon.setImageResource(R.drawable.sun)
                        }
                    }
                    "多云" -> {
                        mBinding.apply {
                            weatherValue.text = "cloudy $weather"
                            weatherIcon.setImageResource(R.drawable.partly_cloudy)
                        }
                    }
                    "阵雨" -> {
                        mBinding.apply {
                            weatherValue.text = "showers $weather"
                            weatherIcon.setImageResource(R.drawable.light_rain)
                        }
                    }
                    "阴" -> {
                        mBinding.apply {
                            weatherValue.text = "cloudy $weather"
                            weatherIcon.setImageResource(R.drawable.partly_cloudy)
                        }
                    }
                }
            }

            placeInf.observe(viewLifecycleOwner) {
                mBinding.cityName.text = it.name
            }

            location.observe(viewLifecycleOwner) { location ->
                QWeather.getWeather10D(requireActivity() ,location,object :QWeather.OnResultWeatherDailyListener{
                    override fun onError(throwable: Throwable?) {
                        throwable?.printStackTrace()
                    }

                    override fun onSuccess(weatherDailyBean: WeatherDailyBean?) {
                        val daily = weatherDailyBean?.daily
                        if(null != daily){
                            mViewModel.updateDailyWeathers(daily)
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
                    mBinding.dailyWeatherList.visibility = View.VISIBLE
                    mBinding.progressBar.visibility = View.GONE
                } else {
                    mBinding.dailyWeatherList.visibility = View.GONE
                    mBinding.progressBar.visibility = View.VISIBLE
                }
            }

            spin.observe(viewLifecycleOwner) {
                layoutManager = when (it) {
                    true ->
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    false ->
                        LinearLayoutManager(requireActivity())
                }
                mBinding.dailyWeatherList.layoutManager = layoutManager
            }
        }
    }

}