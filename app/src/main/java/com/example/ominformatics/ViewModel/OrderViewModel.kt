package com.example.ominformatics.ViewModel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.ominformatics.DataSource.ApiClient
import com.example.ominformatics.DataSource.OrderList
import com.example.ominformatics.DataSource.OrderModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.awaitResponse

class OrderViewModel : ViewModel() {

    private val TAG = "OrderViewModel"

    private val orderList = MutableLiveData<List<OrderList>>()

    fun getOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiClient.apiService.getOrderList().awaitResponse()

            if (response.isSuccessful()) {
                val root: OrderModel = response.body()!!
                orderList.postValue(root.orderlist)
            } else {
                Log.e(TAG, "Failed to fetch product details")
            }

        }
    }

    fun observableOrderList() : MutableLiveData<List<OrderList>> {
        return orderList
    }

}