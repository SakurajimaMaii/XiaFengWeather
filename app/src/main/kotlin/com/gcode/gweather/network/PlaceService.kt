package com.gcode.gweather.network

import com.gcode.gweather.model.PlaceResponse
import com.gcode.gweather.utils.AppUtils
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PlaceService {
    /**
     * 调用searchPlace方法时Retrofit会自动发送GET请求
     * @param location String
     * @return Call<DataResponse>
     */
    @GET("v3/location/search.json?key=${AppUtils.TOKEN}&language=zh-Hans&unit=c")
    fun searchPlace(@Query("q") location: String): Call<PlaceResponse>
}