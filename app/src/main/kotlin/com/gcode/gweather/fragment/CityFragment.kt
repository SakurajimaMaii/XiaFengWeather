package com.gcode.gweather.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.gcode.gweather.BR
import com.gcode.gweather.databinding.CityFragmentBinding
import com.gcode.gweather.model.PlaceInf
import com.gcode.gweather.viewModel.HomeActivityViewModel
import com.gcode.vastadapter.base.VastBindAdapter
import com.gcode.vastadapter.interfaces.VastBindAdapterItem
import com.gcode.vasttools.extension.cast
import com.gcode.vasttools.fragment.VastVbVmFragment

class CityFragment : VastVbVmFragment<CityFragmentBinding,HomeActivityViewModel>() {

    private class DataBindingAdapter(
        dataSource: MutableList<VastBindAdapterItem>,
        mContext:Context
    ) : VastBindAdapter(dataSource,mContext) {

        override fun setVariableId(): Int {
            return BR.item
        }

    }

    private val cityList: MutableList<VastBindAdapterItem> = ArrayList()
    private val adapter = DataBindingAdapter(cityList,requireActivity())
    private lateinit var layoutManager: LinearLayoutManager

    override fun initView(view:View,savedInstanceState: Bundle?) {
        layoutManager = LinearLayoutManager(mBinding.root.context)
        //设置设备搜索
        mBinding.searchView.isSubmitButtonEnabled = true
        mBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { mViewModel.searchCity(it) }
                return false
            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        adapter.setOnItemClickListener(object : VastBindAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                val city = cast<PlaceInf>(cityList[position])
                mViewModel.setPlaceInf(city)
                mViewModel.setCurrentPage(0)
            }
        })
        mBinding.cityList.adapter = adapter
        mBinding.cityList.layoutManager = layoutManager

        mViewModel.cityListJson.observe(requireActivity()) { result ->
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