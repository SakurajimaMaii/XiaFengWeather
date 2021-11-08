package com.gcode.gweather.adapter

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gcode.gweather.fragment.CityFragment
import com.gcode.gweather.fragment.DataFragment
import com.gcode.gweather.fragment.HomeFragment

class MainActFragmentAdapter(appCompatActivity: AppCompatActivity) :
    FragmentStateAdapter(appCompatActivity) {

    /**
     * 主页Fragment索引
     */
    enum class FragmentIndex{
        Home,Data,City
    }

    companion object{
        //主页面Fragment的数量
        const val num_pages = 3
    }

    override fun getItemCount(): Int {
        return num_pages
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            FragmentIndex.Home.ordinal->HomeFragment()
            FragmentIndex.Data.ordinal->DataFragment()
            FragmentIndex.City.ordinal->CityFragment()
            else -> HomeFragment()
        }
    }
}