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
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.gcode.gweather.R
import com.gcode.gweather.adapter.MainActFragmentAdapter
import com.gcode.gweather.databinding.HomeActivityBinding
import com.gcode.gweather.utils.AmapUtils
import com.gcode.gweather.viewModel.HomeActivityViewModel
import com.gcode.vasttools.utils.LogUtils
import com.gcode.vasttools.utils.MsgWindowUtils
import com.permissionx.guolindev.PermissionX
import kotlinx.coroutines.launch
import nl.joery.animatedbottombar.AnimatedBottomBar


class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeActivityBinding

    private val viewModel by lazy {
        ViewModelProvider(this)[HomeActivityViewModel::class.java]
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            LogUtils.d(this.javaClass, this.localClassName, "resultCode is ${result.resultCode}")
            if (result.resultCode == Activity.RESULT_CANCELED || result.resultCode == Activity.RESULT_CANCELED) {
                isGpsOPen()
            }
        }

    private val tag = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.home_activity)

        LogUtils.logEnabled = false

        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    LogUtils.i(this.javaClass, tag, "All permissions are granted")
                } else {
                    MsgWindowUtils.showShortMsg(this, "These permissions are denied: $deniedList")
                }
            }

        AmapUtils.startClient()

        //设置顶部栏
        setSupportActionBar(binding.homeToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        isGpsOPen()

        viewModel.currentPage.observe(this) {
            binding.viewPager.currentItem = it
            binding.bottomNavView.selectTabAt(it)
        }

        val adapter = MainActFragmentAdapter(this)
        binding.viewPager.apply {
            this.adapter = adapter

            isUserInputEnabled = false
        }

        binding.bottomNavView.setOnTabSelectListener(object :
            AnimatedBottomBar.OnTabSelectListener {
            override fun onTabSelected(
                lastIndex: Int,
                lastTab: AnimatedBottomBar.Tab?,
                newIndex: Int,
                newTab: AnimatedBottomBar.Tab
            ) {
                binding.viewPager.currentItem = newIndex
            }

            // An optional method that will be fired whenever an already selected tab has been selected again.
            override fun onTabReselected(index: Int, tab: AnimatedBottomBar.Tab) {
                binding.viewPager.currentItem = index
            }
        })
    }

    override fun onBackPressed() {
        if (binding.viewPager.currentItem == 0) {
            super.onBackPressed()
        } else {
            binding.viewPager.currentItem = binding.viewPager.currentItem - 1
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
                viewModel.spin.value?.let { viewModel.spinList(!it) }
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
            LogUtils.d(this.javaClass, tag, "定位已经打开")
            var location: String
            lifecycleScope.launch {
                location = AmapUtils.getLocation()
                viewModel.searchPlaces(location)
                viewModel.setGpsStatus(true)
            }
        } else {
            viewModel.setGpsStatus(false)
            LogUtils.d(this.javaClass, tag, "定位未打开")
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
                            LogUtils.e(this.javaClass, tag, ex.message.toString())
                        }
                    }
                }
                .setNegativeButton("取消", null)
                .create().show()
        }
    }
}