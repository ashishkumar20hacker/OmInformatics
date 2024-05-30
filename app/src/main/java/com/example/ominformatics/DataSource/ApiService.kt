package com.example.ominformatics.DataSource

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    @GET("orderlist.php")
    fun getOrderList(): Call<OrderModel>

}