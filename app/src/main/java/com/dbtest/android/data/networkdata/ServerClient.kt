package com.dbtest.android.data.networkdata

import androidx.viewbinding.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServerClient {

    private var RETROFIT: Retrofit? = null

    val apiClient: ApiService
        get() {
            if (RETROFIT == null) {
                RETROFIT = Retrofit.Builder()
                    .baseUrl("https://reqres.in")
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return RETROFIT!!.create(ApiService::class.java)
        }

    private val httpClient: OkHttpClient
        get() {
            val loggingInterceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG)
                loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            else loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
            return OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(loggingInterceptor)
                .build()
        }
}