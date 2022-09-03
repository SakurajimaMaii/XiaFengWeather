package com.gcode.gweather.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceCreator {

    //天气请求根路径
    private const val WEATHER_BASE_URL = "https://api.seniverse.com/"

    private val weatherRetrofit = Retrofit.Builder()
        .baseUrl(WEATHER_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun <T> weatherCreate(serviceClass: Class<T>): T = weatherRetrofit.create(serviceClass)

    //inline fun <reified T> create():T = create(T::class.java)
}