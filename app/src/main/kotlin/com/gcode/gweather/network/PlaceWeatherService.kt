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

package com.gcode.gweather.network

import com.gcode.gweather.model.DailyDataResponse
import com.gcode.gweather.model.NowDataResponse
import com.gcode.gweather.utils.AppUtils
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceWeatherService {
    /**
     * 查询当日天气
     * @param location String
     * @return Call<DataResponse>
     */
    @GET("v3/weather/now.json?key=${AppUtils.TOKEN}&language=zh-Hans&unit=c")
    fun searchPlaceWeather(@Query("location") location: String): Call<NowDataResponse>

    /**
     * 调查最近几天的天气
     * @param location String
     * @return Call<DataResponse>
     */
    @GET("v3/weather/daily.json?key=${AppUtils.TOKEN}&language=zh-Hans&unit=c&start=0&days=5")
    fun searchDailyWeather(@Query("location") location: String): Call<DailyDataResponse>
}