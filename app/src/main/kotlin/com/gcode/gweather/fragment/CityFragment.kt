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
import com.gcode.vasttools.utils.StrUtils
import com.qweather.sdk.bean.geo.GeoBean
import com.qweather.sdk.view.QWeather

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
    private lateinit var adapter:DataBindingAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun initView(view:View,savedInstanceState: Bundle?) {
        adapter = DataBindingAdapter(cityList,requireActivity())
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

        mViewModel.cityCode.observe(requireActivity()){ code->
            QWeather.getGeoCityLookup(requireActivity(),code,object :QWeather.OnResultGeoListener{
                override fun onError(p0: Throwable?) {
                    p0?.printStackTrace()
                }

                override fun onSuccess(geoBean: GeoBean?) {
                    if(null != geoBean){
                        val cities = geoBean.locationBean
                        cityList.clear()
                        if (cities != null) {
                            for (item in cities) {
                                cityList.add(PlaceInf(
                                    item.id,
                                    item.name,
                                    item.country,
                                    StrUtils.strConcat(item.adm1,"/",item.adm2),
                                    item.utcOffset
                                ))
                            }
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            })
        }
    }
}