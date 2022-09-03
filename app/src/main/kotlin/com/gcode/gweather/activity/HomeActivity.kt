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

package com.gcode.gweather.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gcode.gweather.R
import com.gcode.gweather.databinding.HomeActivityBinding
import com.gcode.gweather.fragment.CityFragment
import com.gcode.gweather.fragment.DataFragment
import com.gcode.gweather.fragment.HomeFragment
import com.gcode.gweather.utils.AmapUtils
import com.gcode.gweather.viewModel.HomeActivityViewModel
import com.gcode.vasttools.activity.VastVbVmActivity
import com.gcode.vasttools.adapter.VastFragmentAdapter
import com.gcode.vasttools.utils.LogUtils
import com.gcode.vasttools.utils.ToastUtils
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar


class HomeActivity:VastVbVmActivity<HomeActivityBinding,HomeActivityViewModel>() {

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_CANCELED || result.resultCode == Activity.RESULT_CANCELED) {
                isGpsOPen()
            }
        }

    override fun initView(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    LogUtils.i(defaultTag, "All permissions are granted")
                } else {
                    ToastUtils.showShortMsg(this, "These permissions are denied: $deniedList")
                }
            }

        AmapUtils.startClient()

        //设置顶部栏
        setSupportActionBar(mBinding.homeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isGpsOPen()

        mViewModel.currentPage.observe(this) {
            mBinding.viewPager.currentItem = it
            mBinding.bottomNavView.selectTabAt(it)
        }

        mBinding.viewPager.apply {
            this.adapter = VastFragmentAdapter(this@HomeActivity ,ArrayList<Fragment>().apply {
                add(HomeFragment())
                add(DataFragment())
                add(CityFragment())
            })
            isUserInputEnabled = false
        }

        mBinding.bottomNavView.setOnTabSelectListener(object :
            AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                mBinding.viewPager.currentItem = newIndex
            }

            // An optional method that will be fired whenever an already selected tab has been selected again.
            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
                mBinding.viewPager.currentItem = index
            }
        })
    }

    override fun onBackPressed() {
        if (mBinding.viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            mBinding.viewPager.currentItem = mBinding.viewPager.currentItem - 1
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.actionbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.spin_list -> {
                mViewModel.spin.value?.let { mViewModel.spinList(!it) }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        AmapUtils.destroyClient()
    }

    private fun isGpsOPen() {
        //判断GPS是否打开
        if (AmapUtils.isOPen(this)) {
            var location: String
            lifecycleScope.launch {
                location = AmapUtils.getLocation()
                mViewModel.searchPlaces(location)
                mViewModel.setGpsStatus(true)
            }
        } else {
            mViewModel.setGpsStatus(false)
            AlertDialog.Builder(this)
                .setTitle("提示消息")
                .setMessage("定位未打开,请前往设置界面打开")
                .setPositiveButton(
                    "确定"
                ) { _, _ ->
                    val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    if (packageManager.resolveActivity(
                            settingsIntent,
                            PackageManager.MATCH_DEFAULT_ONLY
                        ) != null
                    ) {
                        try {
                            startForResult.launch(settingsIntent)
                        } catch (ex: ActivityNotFoundException) {
                            ex.printStackTrace()
                        }
                    }
                }
                .setNegativeButton("取消", null)
                .create().show()
        }
    }
}