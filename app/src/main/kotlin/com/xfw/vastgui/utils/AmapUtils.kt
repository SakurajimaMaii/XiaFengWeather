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

package com.xfw.vastgui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.ave.vastgui.tools.content.ContextHelper
import com.xfw.vastgui.log.mLogFactory
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 高德地图工具类
 */
object AmapUtils {

    private val mLogger = mLogFactory.getLog(AmapUtils::class.java)

    private lateinit var mLocationOption: AMapLocationClientOption

    @SuppressLint("StaticFieldLeak")
    private lateinit var mLocationClient: AMapLocationClient

    //经纬度信息
    private var latitude: Float = 0f //获取纬度
    private var longitude: Float = 0f //获取经度

    //获取位置描述
    private var address: String = ""
    private var continuation: Continuation<Unit>? = null

    fun isOPen(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // 通过GPS卫星定位，定位级别可以精确到街（通过24颗卫星定位，在室外和空旷的地方定位准确、速度快）
        val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        // 通过WLAN或移动网络(3G/2G)确定的位置（也称作AGPS，辅助GPS定位。主要用于在室内或遮盖物（建筑群或茂密的深林等）密集的地方定位）
        val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return gps && network
    }

    //https://docs.seniverse.com/api/start/common.html#%E5%9C%B0%E7%82%B9-location
    /**
     * 将请求到的数据转换成Api location中要求格式
     * 详情参照上方链接
     * @return String
     */
    suspend fun getLocation(): String {
        /**
         * 设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
         */
        mLocationClient.apply {
            stopLocation()
            startLocation()
        }
        suspendCoroutine {
            continuation = it
        }
        return String.format("%.2f", longitude) + "," + String.format("%.2f", latitude)
    }

    /**
     * 启动定位客户端，同时启动本地定位服务。
     */
    fun startClient() {
        //声明AMapLocationClientOption对象并初始化
        mLocationOption = AMapLocationClientOption().apply {
            //设置定位场景，目前支持三种场景（签到、出行、运动，默认无场景）
            locationPurpose = AMapLocationClientOption.AMapLocationPurpose.SignIn
            //设置单次
            isOnceLocation = true
            //设置精度
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        }
        //https://lbs.amap.com/api/android-location-sdk/guide/create-project/dev-attention
        AMapLocationClient.updatePrivacyShow(ContextHelper.getAppContext(), true, true)
        AMapLocationClient.updatePrivacyAgree(ContextHelper.getAppContext(), true)
        //声明AMapLocationClient类对象 初始化定位
        mLocationClient = AMapLocationClient(ContextHelper.getAppContext()).apply {
            //设置定位回调监听
            setLocationListener(mAMapLocationListener)
            setLocationOption(mLocationOption)
            startLocation()
        }
    }

    /**
     * 销毁定位客户端，同时销毁本地定位服务。
     */
    fun destroyClient() {
        mLocationClient.onDestroy()
    }

    /**
     * 接收数据并解析
     */
    private val mAMapLocationListener = AMapLocationListener { amapLocation ->
        if (amapLocation != null) {
            if (amapLocation.errorCode == 0) {
                //https://lbs.amap.com/api/android-location-sdk/guide/android-location/getlocation
                //更多参数可以参考上方链接 可在其中解析AmapLocation获取相应内容。
                latitude = amapLocation.latitude.toFloat()//获取纬度
                longitude = amapLocation.longitude.toFloat()//获取经度
                address = amapLocation.address.toString()
                continuation?.resume(Unit)
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                mLogger.e(
                    "AmapUtilsError",
                    "location Error, ErrCode:" + amapLocation.errorCode + ", errInfo:" + amapLocation.errorInfo
                )
                continuation?.resumeWithException(RuntimeException(amapLocation.errorInfo))
            }
        }
    }
}