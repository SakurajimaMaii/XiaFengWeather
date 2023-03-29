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
import com.ave.vastgui.core.extension.cast
import com.ave.vastgui.tools.fragment.VastVbVmFragment
import com.ave.vastgui.tools.utils.StrUtils
import com.gcode.gweather.BR
import com.gcode.gweather.databinding.FragmentCityBinding
import com.gcode.gweather.model.PlaceInf
import com.gcode.gweather.viewModel.HomeActivityViewModel
import com.gcode.vastadapter.base.VastBindAdapter
import com.gcode.vastadapter.interfaces.VastBindAdapterItem
import com.qweather.sdk.bean.geo.GeoBean
import com.qweather.sdk.view.QWeather

class CityFragment : VastVbVmFragment<FragmentCityBinding, HomeActivityViewModel>() {

    private class DataBindingAdapter(
        dataSource: MutableList<VastBindAdapterItem>,
        mContext: Context
    ) : VastBindAdapter(dataSource, mContext) {

        override fun setVariableId(): Int {
            return BR.item
        }

    }

    private val cityList: MutableList<VastBindAdapterItem> = ArrayList()
    private lateinit var adapter: DataBindingAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DataBindingAdapter(cityList, requireActivity())
        layoutManager = LinearLayoutManager(getBinding().root.context)
        //设置设备搜索
        getBinding().searchView.isSubmitButtonEnabled = true
        getBinding().searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { getViewModel().searchCity(it) }
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
                getViewModel().setPlaceInf(city)
                getViewModel().setCurrentPage(0)
            }
        })

        getBinding().cityList.adapter = adapter
        getBinding().cityList.layoutManager = layoutManager

        getViewModel().cityCode.observe(requireActivity()) { code ->
            QWeather.getGeoCityLookup(
                requireActivity(),
                code,
                object : QWeather.OnResultGeoListener {
                    override fun onError(p0: Throwable?) {
                        p0?.printStackTrace()
                    }

                    override fun onSuccess(geoBean: GeoBean?) {
                        if (null != geoBean) {
                            val cities = geoBean.locationBean
                            getViewModel().updateDailyCities(cities)
                        }
                    }
                })
        }

        getViewModel().cities.observe(requireActivity()) { cities ->
            cityList.clear()
            if (cities != null) {
                for (item in cities) {
                    cityList.add(
                        PlaceInf(
                            item.id,
                            item.name,
                            item.country,
                            StrUtils.strConcat(item.adm1, "/", item.adm2),
                            item.utcOffset
                        )
                    )
                }
            }
            adapter.notifyDataSetChanged()
        }
    }
}