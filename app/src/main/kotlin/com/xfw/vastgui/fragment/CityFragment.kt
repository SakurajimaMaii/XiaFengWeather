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

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ave.vastgui.adapter.VastBindAdapter
import com.ave.vastgui.adapter.base.ItemWrapper
import com.ave.vastgui.tools.fragment.VastVbVmFragment
import com.xfw.vastgui.BR
import com.xfw.vastgui.R
import com.xfw.vastgui.databinding.FragmentCityBinding
import com.xfw.vastgui.viewModel.HomeActivityViewModel
import com.qweather.sdk.bean.geo.GeoBean
import kotlinx.coroutines.launch

class CityFragment : VastVbVmFragment<FragmentCityBinding, HomeActivityViewModel>() {

    private class PlaceAdapter(context: Context) :
        VastBindAdapter<GeoBean.LocationBean>(context, BR.location) {
        fun addCity(city: GeoBean.LocationBean) {
            val index = itemCount
            mDataSource.add(
                index, ItemWrapper(city, R.layout.item_city_information)
            )
            notifyItemChanged(index)
        }

        fun clearAll() {
            val size = mDataSource.size
            mDataSource.clear()
            notifyItemMoved(0, size)
        }
    }

    private lateinit var mAdapter: PlaceAdapter
    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = PlaceAdapter(requireActivity())
        layoutManager = LinearLayoutManager(getBinding().root.context)
        //设置设备搜索
        getBinding().searchView.isSubmitButtonEnabled = true
        getBinding().searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { getViewModel().searchCities(it) }
                return false
            }

            @RequiresApi(api = Build.VERSION_CODES.Q)
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        getBinding().cityList.adapter = mAdapter.apply {
            setOnItemClickListener { _, _, wrapper ->
                getViewModel().searchPlaces(wrapper.getData().id)
                getViewModel().setCurrentPage(0)
            }
        }
        getBinding().cityList.layoutManager = layoutManager

        lifecycleScope.launch {
            getViewModel().geoCities.collect { cities ->
                if (cities.isNullOrEmpty()) return@collect
                mAdapter.clearAll()
                cities.forEach { locationBean ->
                    mAdapter.addCity(locationBean)
                }
            }
        }
    }
}