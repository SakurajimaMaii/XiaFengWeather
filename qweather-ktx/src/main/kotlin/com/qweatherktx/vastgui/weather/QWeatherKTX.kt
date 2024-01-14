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

package com.qweatherktx.vastgui.weather

import android.content.Context
import com.qweather.sdk.bean.base.Lang
import com.qweather.sdk.bean.base.Range
import com.qweather.sdk.bean.base.Unit
import com.qweather.sdk.bean.geo.GeoBean
import com.qweather.sdk.bean.history.HistoricalAirBean
import com.qweather.sdk.bean.weather.WeatherDailyBean
import com.qweather.sdk.bean.weather.WeatherNowBean
import com.qweather.sdk.view.QWeather
import com.qweather.sdk.view.QWeather.OnResultAirHistoricalBeanListener
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

// Author: Vast Gui
// Email: guihy2019@gmail.com
// Date: 2024/1/14 0:36

object QWeatherKTX {

    /**
     * [城市搜索](https://dev.qweather.com/docs/android-sdk/geoapi/android-city-lookup/)
     */
    suspend fun getGeoCityLookup(
        context: Context,
        location: String,
        range: Range? = null,
        number: Int = 10,
        lang: Lang? = null
    ): Result<GeoBean> = suspendCoroutine { coroutineScope ->
        QWeather.getGeoCityLookup(context, location, range, number, lang, object :
            QWeather.OnResultGeoListener {
            override fun onError(throwable: Throwable) {
                coroutineScope.resumeWith(Result.failure(throwable))
            }

            override fun onSuccess(geobean: GeoBean) {
                coroutineScope.resume(Result.success(geobean))
            }
        })
    }

    /**
     * [实时天气](https://dev.qweather.com/docs/android-sdk/weather/android-weather-now/)
     */
    suspend fun getWeatherNow(
        context: Context,
        location: String,
        lang: Lang? = null,
        unit: Unit? = null
    ): Result<WeatherNowBean> = suspendCoroutine { coroutineScope ->
        QWeather.getWeatherNow(
            context, location, lang, unit,
            object : QWeather.OnResultWeatherNowListener {
                override fun onError(throwable: Throwable) {
                    coroutineScope.resumeWith(Result.failure(throwable))
                }

                override fun onSuccess(weatherNowBean: WeatherNowBean) {
                    coroutineScope.resume(Result.success(weatherNowBean))
                }
            })
    }

    /**
     * [7天预报天气数据](https://dev.qweather.com/docs/android-sdk/weather/android-weather-daily-forecast/)
     */
    suspend fun getWeather7D(
        context: Context, location: String, lang: Lang? = null,
        unit: Unit? = null
    ): Result<WeatherDailyBean> =
        suspendCoroutine { coroutineScope ->
            QWeather.getWeather7D(
                context, location, lang, unit,
                object : QWeather.OnResultWeatherDailyListener {
                    override fun onError(throwable: Throwable) {
                        coroutineScope.resumeWith(Result.failure(throwable))
                    }

                    override fun onSuccess(weatherdailybean: WeatherDailyBean) {
                        coroutineScope.resume(Result.success(weatherdailybean))
                    }
                })
        }

    /**
     * [历史空气质量](https://dev.qweather.com/docs/android-sdk/time-machine/android-time-machine-air/)
     */
    suspend fun getHistoricalAir(
        context: Context,
        location: String,
        date: String,
        lang: Lang? = null,
        unit: Unit? = null
    ): Result<HistoricalAirBean> =
        suspendCoroutine { coroutineScope ->
            QWeather.getHistoricalAir(context, location, date, lang, unit,
                object : OnResultAirHistoricalBeanListener {
                    override fun onError(throwable: Throwable) {
                        coroutineScope.resumeWith(Result.failure(throwable))
                    }

                    override fun onSuccess(historicalAirBean: HistoricalAirBean) {
                        coroutineScope.resume(Result.success(historicalAirBean))
                    }
                })
        }
}