package com.gcode.gweather.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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

            dailyWeatherResult.observe(viewLifecycleOwner) { res ->
                val list = res.getOrNull()
                if (list != null) {
                    val daily = list[0].daily
                    dailyWeatherList.clear()
                    for (item in daily) {
                        dailyWeatherList.add(
                            SimpleDailyWeather(
                                item.date,
                                item.text_day,
                                item.high,
                                item.low,
                                item.windSpeed,
                                item.humidity
                            )
                        )
                    }
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