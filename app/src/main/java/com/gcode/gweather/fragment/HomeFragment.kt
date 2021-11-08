package com.gcode.gweather.fragment

import android.annotation.SuppressLint
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
import com.gcode.gweather.utils.AppUtils
import com.gcode.gweather.viewModel.HomeActivityViewModel
import com.gcode.vastadapter.BaseGcodeBindingAdapter
import com.gcode.vasttools.utils.MsgWindowUtils

class HomeFragment : Fragment() {

    private class DataBindingAdapter(items: MutableList<SimpleDailyWeather>) :
        BaseGcodeBindingAdapter<SimpleDailyWeather>(items) {
        override fun setVariableId(): Int {
            return BR.item
        }
    }

    private lateinit var binding: HomeFragmentBinding

    //获取ViewModel
    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[HomeActivityViewModel::class.java]
    }

    private val dailyWeatherList: MutableList<SimpleDailyWeather> = ArrayList()
    private val adapter = DataBindingAdapter(dailyWeatherList)
    private lateinit var layoutManager: RecyclerView.LayoutManager

    //加载布局
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_fragment,
            container,
            false
        )
        layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        return binding.root
    }

    @SuppressLint("SetTextI18n", "NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()

        val bundle = arguments
        val username = bundle?.getString("username")
        MsgWindowUtils.showShortMsg(AppUtils.context, username.toString())

        binding.dailyWeatherList.adapter = adapter
        binding.dailyWeatherList.layoutManager = layoutManager

        viewModel.apply {
            /**
             * 更新温度文字描述
             */
            temperature.observe(viewLifecycleOwner) { temperatureValue ->
                binding.temperatureValue.text = "$temperatureValue°"
            }

            /**
             * 更新天气文字描述
             */
            weather.observe(viewLifecycleOwner) { weather ->
                when (weather) {
                    "晴" -> {
                        binding.apply {
                            weatherValue.text = "sun $weather"
                            weatherIcon.setImageResource(R.drawable.sun)
                        }
                    }
                    "多云" -> {
                        binding.apply {
                            weatherValue.text = "cloudy $weather"
                            weatherIcon.setImageResource(R.drawable.partly_cloudy)
                        }
                    }
                    "阵雨" -> {
                        binding.apply {
                            weatherValue.text = "showers $weather"
                            weatherIcon.setImageResource(R.drawable.light_rain)
                        }
                    }
                    "阴" -> {
                        binding.apply {
                            weatherValue.text = "cloudy $weather"
                            weatherIcon.setImageResource(R.drawable.partly_cloudy)
                        }
                    }
                }
            }

            placeInf.observe(viewLifecycleOwner) {
                binding.cityName.text = it.name
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
                    binding.dailyWeatherList.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.GONE
                } else {
                    binding.dailyWeatherList.visibility = View.GONE
                    binding.progressBar.visibility = View.VISIBLE
                }
            }

            spin.observe(viewLifecycleOwner) {
                layoutManager = when (it) {
                    true ->
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                    false ->
                        LinearLayoutManager(binding.root.context)
                }
                binding.dailyWeatherList.layoutManager = layoutManager
            }
        }
    }

}