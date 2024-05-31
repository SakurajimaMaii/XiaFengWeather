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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.ave.vastgui.netstatelayout.NetStateMgr
import com.ave.vastgui.netstatelayout.listener.OnNetErrorListener
import com.ave.vastgui.tools.annotation.ExperimentalView
import com.ave.vastgui.tools.text.appendablestylestring.AppendableStyle
import com.ave.vastgui.tools.text.appendablestylestring.appendableStyleScope
import com.ave.vastgui.tools.text.appendablestylestring.withStyle
import com.ave.vastgui.tools.utils.ColorUtils
import com.ave.vastgui.tools.utils.IntentUtils
import com.ave.vastgui.tools.utils.NetStateUtils
import com.ave.vastgui.tools.view.extension.gone
import com.ave.vastgui.tools.view.extension.visible
import com.ave.vastgui.tools.view.masklayout.MaskAnimation
import com.ave.vastgui.tools.view.masklayout.MaskLayout
import com.ave.vastgui.tools.viewbinding.viewBinding
import com.kongzue.dialogx.dialogs.BottomDialog
import com.kongzue.dialogx.dialogs.InputDialog
import com.qwsdk.vastgui.QWSdk
import com.xfw.vastgui.R
import com.xfw.vastgui.databinding.ActivityInitBinding
import com.xfw.vastgui.log.mLogFactory
import com.xfw.vastgui.sp.AppSp
import com.xfw.vastgui.utils.relaunchApp
import com.xfw.vastgui.viewModel.AuthorityState
import com.xfw.vastgui.viewModel.InitViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2024/5/27 13:23
// Description: 初始化页面

@OptIn(ExperimentalView::class)
class InitActivity : ComponentActivity(R.layout.activity_init) {

    private val mViewModel: InitViewModel by viewModels()
    private val mLogger = mLogFactory.getLog(InitActivity::class.java)
    private val mNetLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (RESULT_OK == result.resultCode) {
                if (NetStateUtils.isNetworkAvailable(this)) {
                    mBinding.netStateLayout.showSuccess()
                }
            }
        }
    private val mBinding by viewBinding(ActivityInitBinding::bind)
    private lateinit var mNetStateMgr: NetStateMgr

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.authorityState.observe(this) { state ->
            mLogger.d(TAG, "AuthorityState 监听到变化：$state")
            if (state is AuthorityState.Pass) {
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
            }
        }
        initView()
        authentication()
    }

    /** 通过调用接口来测试是否拥有权限 */
    private fun authentication() {
        if (NetStateUtils.isNetworkAvailable(this)) {
            mViewModel.authentication()
        } else {
            mBinding.netStateLayout.showNetError(RuntimeException("当前网络不可用"))
        }
    }

    private fun initView() {
        // 设置标题
        mBinding.title.text = appendableStyleScope {
            append("欢迎使用夏风天气\n")
            val size = resources.getDimensionPixelSize(R.dimen.sp_20)
            withStyle(AppendableStyle(fontSize = size)) {
                append("选择你的")
                val color = getColor(R.color.md_theme_primary)
                withStyle(AppendableStyle(foreColor = color)) {
                    append("订阅方案")
                }
            }
        }
        // 设置订阅描述
        mBinding.subscription.text = appendableStyleScope {
            append("对订阅存在疑问？")
            val color = getColor(R.color.md_theme_primary)
            withStyle(AppendableStyle(foreColor = color)) {
                append("了解更多")
            }
        }
        mBinding.subscription.setOnClickListener {
            BottomDialog.show(
                "订阅", "应用的数据来源于和风天气©不同的订阅方式意味着你能获取的" +
                        "数据不同，点击跳转按钮来了解不同订阅方案间的区别。"
            ).setOkButton("跳转") { _, view ->
                IntentUtils.openWebPage(
                    view.context,
                    "https://dev.qweather.com/docs/finance/subscription/"
                )
                false
            }
            choose()
        }
        // 免费订阅
        mBinding.free.setOnClickListener {
            mBinding.maskLayout.apply {
                it.updateCoordinate(this)
                activeMask(MaskAnimation.EXPANDED, object : MaskLayout.MaskAnimationListener {
                    override fun onMaskComplete() {
                        mBinding.standard.gone()
                        mBinding.free.gone()
                        mBinding.root.setBackgroundColor(ColorUtils.colorHex2Int("#b8e994"))
                        mBinding.icon.setImageDrawable(
                            AppCompatResources.getDrawable(it.context, R.drawable.img_free)
                        )
                    }

                    override fun onMaskFinished() {
                        AppSp.plan = QWSdk.Plan.Free.ordinal
                        showKeyInput()
                    }
                })
            }
        }
        // 标准订阅
        mBinding.standard.setOnClickListener { standard ->
            mBinding.maskLayout.apply {
                standard.updateCoordinate(this)
                activeMask(MaskAnimation.EXPANDED, object : MaskLayout.MaskAnimationListener {
                    override fun onMaskComplete() {
                        mBinding.free.gone()
                        mBinding.standard.gone()
                        mBinding.root.setBackgroundColor(ColorUtils.colorHex2Int("#f1f2f6"))
                        mBinding.icon.setImageResource(R.drawable.img_standard)
                    }

                    override fun onMaskFinished() {
                        AppSp.plan = QWSdk.Plan.Standard.ordinal
                        showKeyInput()
                    }
                })
            }
        }
        // 网络状态页面
        mNetStateMgr = NetStateMgr(this).apply {
            setOnNetErrorListener(object : OnNetErrorListener {
                override fun onNetErrorClick() {
                    mNetLauncher.launch(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                }
            })
        }
        mBinding.netStateLayout.setNetStateMgr(mNetStateMgr)
    }

    /** 更新遮罩图显示的坐标 */
    private fun View.updateCoordinate(maskLayout: MaskLayout) = maskLayout.apply {
        mMaskCenterX = this@updateCoordinate.left + this@updateCoordinate.width / 2f
        mMaskCenterY = this@updateCoordinate.top + this@updateCoordinate.height / 2f
    }

    private fun choose() {
        mBinding.maskLayout.apply {
            mBinding.subscription.updateCoordinate(this)
            activeMask(MaskAnimation.EXPANDED, object : MaskLayout.MaskAnimationListener {
                override fun onMaskComplete() {
                    mBinding.standard.visible()
                    mBinding.free.visible()
                    mBinding.root.setBackgroundColor(ColorUtils.colorHex2Int("#FFFFFF"))
                    mBinding.icon.setImageResource(R.drawable.img_choose)
                }

                override fun onMaskFinished() {

                }
            })
        }
    }

    /** 显示激活码输入框 */
    private fun showKeyInput() {
        InputDialog(
            "激活码",
            "请输入你在平台获取的Key",
            "确定", "取消",
            "xxxx-xxxx-xxxx-xxxx-xxxx-xxxx"
        ).setCancelable(false).setOkButton { _, _, content ->
            AppSp.key = content
            lifecycleScope.launch {
                delay(500L) // 确保key保存成功后再重启应用
                relaunchApp(true)
            }
            false
        }.setCancelButton { _, _, _ ->
            choose()
            false
        }.show()
    }

    companion object {
        const val TAG = "InitActivity"
    }
}