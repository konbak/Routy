package com.android.routy.api

import com.android.routy.BuildConfig
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenRouteServicesApi {

    companion object{
        const val BASE_URL = "https://api.openrouteservice.org/"
        const val OPEN_ROUTE_KEY = BuildConfig.OPEN_ROUTE_KEY
    }

    @Headers("Authorization: $OPEN_ROUTE_KEY")
    @POST("optimization")
    suspend fun postRouteOptimization(@Body requestBody: RequestBody): Response<ResponseBody>
}