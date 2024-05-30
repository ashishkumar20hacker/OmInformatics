package com.example.ominformatics.DataSource

import com.example.ominformatics.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitApiClient {

    private const val BASE_URL = "https://ominfo.in/test_app/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(HttpClient.httpClient)
            .build()
    }

}

object ApiClient {
    val apiService : ApiService by lazy {
        RetrofitApiClient.retrofit.create(ApiService::class.java)
    }
}

object HttpClient {

    val httpLoggingInterceptor = HttpLoggingInterceptor().also {
        if (BuildConfig.DEBUG) {
            it.setLevel(HttpLoggingInterceptor.Level.BODY)   // development build
        } else {
            it.setLevel(HttpLoggingInterceptor.Level.NONE)   // production build
        }
    }

    val httpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()
    }

}