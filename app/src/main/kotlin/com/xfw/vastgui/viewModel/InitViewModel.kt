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

package com.xfw.vastgui.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ave.vastgui.tools.utils.DateUtils
import com.qwsdk.vastgui.QWSdk
import com.qwsdk.vastgui.utils.Name
import com.qwsdk.vastgui.utils.exceptions.QWSdkException
import com.xfw.vastgui.App
import com.xfw.vastgui.log.mLogFactory
import com.xfw.vastgui.sp.AppSp
import kotlinx.coroutines.launch

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2024/5/27 22:33
// Description: 
// Documentation:
// Reference:

sealed class AuthorityState {
    /** 默认状态 */
    data object Default : AuthorityState()

    /** 权限校验通过 */
    data object Pass : AuthorityState()

    /** 权限校验失败 */
    data class Deny(val exception: Throwable) : AuthorityState()
}

class InitViewModel : ViewModel() {
    private val mLogger = mLogFactory.getLog(InitViewModel::class.java)
    private val _authorityState: MutableLiveData<AuthorityState> =
        MutableLiveData(AuthorityState.Default)
    val authorityState: LiveData<AuthorityState>
        get() = _authorityState

    fun authentication() {
        // 如果当前是免费订阅
        if (QWSdk.Plan.Free.ordinal == AppSp.plan) {
            viewModelScope.launch {
                App.qw.geo().topCity()
                    .onFailure {
                        mLogger.e("免费订阅鉴权失败：${it.stackTraceToString()}")
                        if (isDeny(it)) {
                            _authorityState.postValue(AuthorityState.Deny(it))
                        }
                    }
                    .onSuccess {
                        _authorityState.postValue(AuthorityState.Pass)
                    }
            }
        }
        // 如果当前是付费订阅
        else if (QWSdk.Plan.Standard.ordinal == AppSp.plan) {
            viewModelScope.launch {
                val geoLookup = App.qw.geo()
                    .cityLookup(Name("北京"))
                    .onFailure {
                        mLogger.e("付费订阅鉴权失败：${it.stackTraceToString()}")
                        if (isDeny(it)) {
                            _authorityState.postValue(AuthorityState.Deny(it))
                        }
                    }.getOrNull()
                if (null != geoLookup) {
                    val date = DateUtils.getDayBeforeOrAfterCurrentTime("yyyyMMdd", -1)
                    App.qw.timeMachine()
                        .airHistory(geoLookup.location[0].getLocationID(), date)
                        .onFailure {
                            mLogger.e("付费订阅鉴权失败：${it.stackTraceToString()}")
                            if (isDeny(it)) {
                                _authorityState.postValue(AuthorityState.Deny(it))
                            }
                        }
                        .onSuccess {
                            _authorityState.postValue(AuthorityState.Pass)
                        }
                }
            }
        }
    }

    /** 如果是指定的错误的话则判断为无权限 */
    private fun isDeny(throwable: Throwable) = when (throwable) {
        is QWSdkException.E401 -> true
        is QWSdkException.E402 -> true
        is QWSdkException.E403 -> true
        else -> false
    }

}