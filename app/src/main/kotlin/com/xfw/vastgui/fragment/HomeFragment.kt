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

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ave.vastgui.adapter.VastBindAdapter
import com.ave.vastgui.adapter.base.ItemWrapper
import com.ave.vastgui.tools.fragment.VastVbVmFragment
import com.ave.vastgui.tools.view.toast.SimpleToast
import com.qwsdk.vastgui.entity.weather.daily.WeatherDaily
import com.xfw.vastgui.BR
import com.xfw.vastgui.R
import com.xfw.vastgui.databinding.FragmentHomeBinding
import com.xfw.vastgui.viewModel.HomeActivityViewModel
import kotlinx.coroutines.launch

class HomeFragment : VastVbVmFragment<FragmentHomeBinding, HomeActivityViewModel>() {

    private class DailyWeatherAdapter(context: Context) :
        VastBindAdapter<WeatherDaily.Daily>(context, BR.dailyWeather) {
        /**
         * 添加当日天气数据
         */
        fun addDailyWeather(daily: WeatherDaily.Daily) {
            val index = itemCount
            mDataSource.add(
                index, ItemWrapper(daily, R.layout.item_daily_weather)
            )
            notifyItemChanged(index)
        }

        fun clearAll() {
            val size = mDataSource.size
            mDataSource.clear()
            notifyItemMoved(0, size)
        }
    }

    private lateinit var mAdapter: DailyWeatherAdapter
    private lateinit var layoutManager: RecyclerView.LayoutManager

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = DailyWeatherAdapter(requireActivity())
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)

        val bundle = arguments
        val username = bundle?.getString("username")
        SimpleToast.showShortMsg(username.toString())

        getBinding().dailyWeatherList.adapter = mAdapter
        getBinding().dailyWeatherList.layoutManager = layoutManager

        lifecycleScope.launch {
            getViewModel().weather7D.collect{
                it?.daily?.apply {
                    mAdapter.clearAll()
                    for (daily in this) {
                        mAdapter.addDailyWeather(daily)
                    }
                } ?: SimpleToast.showShortMsg("近七天天气数据为空")
            }
        }

        lifecycleScope.launch {
            /** 更新温度文字描述 */
            getViewModel().weatherNow.collect{
                if(null == it) return@collect
                getBinding().temperatureValue.text = "${it.now?.temp}°"
                when (it.now?.text) {
                    "晴" -> {
                        getBinding().apply {
                            weatherValue.text = "sun ${it.now?.text}"
                            weatherIcon.setImageResource(R.drawable.sun)
                        }
                    }

                    "多云" -> {
                        getBinding().apply {
                            weatherValue.text = "cloudy ${it.now?.text}"
                            weatherIcon.setImageResource(R.drawable.partly_cloudy)
                        }
                    }

                    "阵雨" -> {
                        getBinding().apply {
                            weatherValue.text = "showers ${it.now?.text}"
                            weatherIcon.setImageResource(R.drawable.light_rain)
                        }
                    }

                    "阴" -> {
                        getBinding().apply {
                            weatherValue.text = "cloudy ${it.now?.text}"
                            weatherIcon.setImageResource(R.drawable.partly_cloudy)
                        }
                    }
                }
            }
        }

        getViewModel().apply {
            isGpsOpen.observe(requireActivity()) {
                if (it) {
                    getBinding().dailyWeatherList.visibility = View.VISIBLE
                    getBinding().progressBar.visibility = View.GONE
                } else {
                    getBinding().dailyWeatherList.visibility = View.GONE
                    getBinding().progressBar.visibility = View.VISIBLE
                }
            }

            spin.observe(requireActivity()) {
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