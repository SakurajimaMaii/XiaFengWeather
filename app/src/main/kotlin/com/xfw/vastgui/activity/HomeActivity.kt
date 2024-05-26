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

package com.xfw.vastgui.activity

import android.app.Activity
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
import com.ave.vastgui.tools.activity.VastVbVmActivity
import com.ave.vastgui.tools.adapter.VastFragmentAdapter
import com.ave.vastgui.tools.utils.permission.Permission
import com.ave.vastgui.tools.utils.permission.requestMultiplePermissions
import com.ave.vastgui.tools.view.dialog.MaterialAlertDialogBuilder
import com.ave.vastgui.tools.view.toast.SimpleToast
import com.qwsdk.vastgui.utils.Coordinate
import com.xfw.vastgui.R
import com.xfw.vastgui.databinding.ActivityHomeBinding
import com.xfw.vastgui.fragment.CityFragment
import com.xfw.vastgui.fragment.DataFragment
import com.xfw.vastgui.fragment.HomeFragment
import com.xfw.vastgui.log.mLogFactory
import com.xfw.vastgui.utils.AmapUtils
import com.xfw.vastgui.viewModel.HomeActivityViewModel
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar

class HomeActivity : VastVbVmActivity<ActivityHomeBinding, HomeActivityViewModel>() {

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_CANCELED || result.resultCode == Activity.RESULT_CANCELED) {
                isGpsOPen()
            }
        }

    private val mLogger = mLogFactory.getLog(HomeActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestMultiplePermissions(arrayOf(Permission.ACCESS_FINE_LOCATION,Permission.ACCESS_COARSE_LOCATION)){
            allGranted = {
                mLogger.i("所有权限已被授予")
            }
            denied = {
                getSnackbar().setText("部分权限${it}被拒绝")
            }
        }

        AmapUtils.startClient()

        //设置顶部栏
        setSupportActionBar(getBinding().homeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isGpsOPen()

        getViewModel().currentPage.observe(this) {
            getBinding().viewPager.currentItem = it
            getBinding().bottomNavView.selectTabAt(it)
        }

        getBinding().viewPager.apply {
            this.adapter = VastFragmentAdapter(this@HomeActivity, ArrayList<Fragment>().apply {
                add(HomeFragment())
                add(DataFragment())
                add(CityFragment())
            })
            isUserInputEnabled = false
        }

        getBinding().bottomNavView.setOnTabSelectListener(object :
            AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                getBinding().viewPager.currentItem = newIndex
            }

            // An optional method that will be fired whenever an already selected tab has been selected again.
            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
                getBinding().viewPager.currentItem = index
            }
        })
    }

    override fun onBackPressed() {
        if (getBinding().viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            getBinding().viewPager.currentItem -= 1
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
                getViewModel().spin.value?.let { getViewModel().spinList(!it) }
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
            var location: Coordinate
            lifecycleScope.launch {
                try {
                    location = AmapUtils.getLocation()
                    getViewModel().searchPlaces(location)
                    getViewModel().setGpsStatus(true)
                } catch (exception: Exception) {
                    SimpleToast.showShortMsg(exception.message.toString())
                }
            }
        } else {
            getViewModel().setGpsStatus(false)
            MaterialAlertDialogBuilder(this)
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