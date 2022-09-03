package com.gcode.gweather.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object Network {
    //天气信息动态代理对象
    private val placeWeatherService = ServiceCreator.weatherCreate(PlaceWeatherService::class.java)

    //天气信息动态代理对象
    private val placeService = ServiceCreator.weatherCreate(PlaceService::class.java)

    //空气质量信息动态代理对象
    private val AQService = ServiceCreator.weatherCreate(AirQualityService::class.java)

    suspend fun searchPlaceWeather(
        location: String
    ) = placeWeatherService.searchPlaceWeather(location).await()

    suspend fun searchDailyWeather(
        location: String
    ) = placeWeatherService.searchDailyWeather(location).await()

    suspend fun searchPlace(
        location: String
    ) = placeService.searchPlace(location).await()

    /**
     * 搜索天气质量
     * @param location String
     * @return AQResponse
     */
    suspend fun searchAirQuality(
        location: String,
        days: Int
    ) = AQService.searchAirQuality(location, days).await()

    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}