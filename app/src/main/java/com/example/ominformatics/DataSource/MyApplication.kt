package com.example.ominformatics.DataSource

import android.app.Application

class MyApplication: Application() {

    companion object {
        lateinit var app : MyApplication
        fun getOrderDao() :OrderDao{
            val orderDao = DatabaseBuilder.getInstance(app.applicationContext).orderDao()
            return orderDao
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }

}