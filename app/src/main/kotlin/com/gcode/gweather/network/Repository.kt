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

import android.util.Log
import androidx.lifecycle.liveData
import kotlinx.coroutines.Dispatchers

object Repository {
    fun searchPlaceWeather(location: String) = liveData(Dispatchers.IO) {
        val result = try {
            val dataResponse = Network.searchPlaceWeather(location)
            if (dataResponse.results.isNotEmpty()) {
                val place = dataResponse.results
                Result.success(place)
            } else {
                Result.failure(RuntimeException("response data array is empty is ${dataResponse.results.isEmpty()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    fun searchDailyWeather(location: String) = liveData(Dispatchers.IO) {
        val result = try {
            val dataResponse = Network.searchDailyWeather(location)
            if (dataResponse.results.isNotEmpty()) {
                val place = dataResponse.results
                Result.success(place)
            } else {
                Result.failure(RuntimeException("response data array is empty is ${dataResponse.results.isEmpty()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    fun searchPlace(location: String) = liveData(Dispatchers.IO) {
        val result = try {
            val dataResponse = Network.searchPlace(location)
            if (dataResponse.results.isNotEmpty()) {
                val place = dataResponse.results
                Result.success(place)
            } else {
                Result.failure(RuntimeException("response data array is empty is ${dataResponse.results.isEmpty()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

    /**
     * 搜索天气数据
     * @param location String 坐标
     * @param days Int 天数
     * @return LiveData<Result<AQResponse>>
     */
    fun searchAirQuality(location: String, days: Int) = liveData(Dispatchers.IO) {
        val result = try {
            val resultResponse = Network.searchAirQuality(location, days)
            if (resultResponse.results.isNotEmpty()) {
                Log.d("AQSuccess", resultResponse.results[0].lastUpdate)
                Result.success(resultResponse)
            } else {
                Log.d("AQError", "searchAirQuality() response data is empty")
                Result.failure(RuntimeException("response data is empty"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
        emit(result)
    }

}