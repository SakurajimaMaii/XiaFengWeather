package com.gcode.gweather.fragment

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.gcode.gweather.BR
import com.gcode.gweather.R
import com.gcode.gweather.databinding.CityFragmentBinding
import com.gcode.gweather.model.PlaceInf
import com.gcode.gweather.viewModel.HomeActivityViewModel
import com.gcode.vastadapter.BaseGcodeBindingAdapter
import com.gcode.vasttools.utils.LogUtils

class CityFragment : Fragment() {

    private class DataBindingAdapter(items: MutableList<PlaceInf>) :
        BaseGcodeBindingAdapter<PlaceInf>(items) {
        override fun setVariableId(): Int {
            return BR.item
        }
    }

    private val viewModel by lazy {
        ViewModelProvider(requireActivity())[HomeActivityViewModel::class.java]
    }

    private lateinit var binding: CityFragmentBinding

    private val cityList: MutableList<PlaceInf> = ArrayList()

    private val adapter = DataBindingAdapter(cityList)
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.city_fragment,
            container,
            false
        )
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onStart() {
        super.onStart()

        layoutManager = LinearLayoutManager(binding.root.context)
        //设置设备搜索
        binding.searchView.isSubmitButtonEnabled = true
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                LogUtils.d(this.javaClass, "CityFragment", query.toString())
                query?.let { viewModel.searchCity(it) }
                return false
            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        adapter.setOnItemClickListener(object : BaseGcodeBindingAdapter.OnItemClickListener {
            override fun onItemClick(itemView: View?, pos: Int, itemId: Long) {
                val city = cityList[pos]
                viewModel.setPlaceInf(city)
                viewModel.setCurrentPage(0)
            }
        })
        binding.cityList.adapter = adapter
        binding.cityList.layoutManager = layoutManager

        viewModel.cityListJson.observe(viewLifecycleOwner) { result ->
            val cities = result.getOrNull()
            cityList.clear()
            if (cities != null) {
                for (item in cities) {
                    cityList.add(item)
                }
            }
            adapter.notifyDataSetChanged()
        }
    }
}