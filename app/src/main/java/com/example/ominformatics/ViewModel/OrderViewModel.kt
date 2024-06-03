package com.example.ominformatics.ViewModel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ominformatics.DataSource.ApiClient
import com.example.ominformatics.DataSource.DbOrderModel
import com.example.ominformatics.DataSource.DeliveryStatus
import com.example.ominformatics.DataSource.MyApplication.Companion.app
import com.example.ominformatics.DataSource.MyApplication.Companion.getOrderDao
import com.example.ominformatics.DataSource.OrderDao
import com.example.ominformatics.DataSource.OrderModel
import com.example.ominformatics.UI.Utils.checkInternetConnection
import com.example.ominformatics.UI.Utils.isLocationPermissionGranted
import com.example.ominformatics.UI.Utils.requestLocationPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class OrderViewModel : ViewModel() {

    private val TAG = "OrderViewModel"

    private var orderDao: OrderDao = getOrderDao()

    fun getOrders() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = ApiClient.apiService.getOrderList().awaitResponse()

            if (response.isSuccessful()) {
                val root: OrderModel = response.body()!!
                for (i in 0..root.orderlist.size - 1) {
                    val order = DbOrderModel(
                        order_id = root.orderlist[i].order_id.toInt(),
                        address = root.orderlist[i].address,
                        customer_name = root.orderlist[i].customer_name,
                        delivery_cost = root.orderlist[i].delivery_cost.toDouble(),
                        latitude = root.orderlist[i].latitude.toDouble(),
                        longitude = root.orderlist[i].longitude.toDouble(),
                        order_no = root.orderlist[i].order_no,
                        delivery_status = DeliveryStatus.Pending.status
                    )
                    orderDao.insert(order)
                }
            } else {
                Log.e(TAG, "Failed to fetch product details")
            }

        }
    }

    fun observableOrderList(): LiveData<List<DbOrderModel>> {
        val prefs: SharedPreferences =
            app.applicationContext.getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE)
        val isFirstTime = prefs.getBoolean("PREF_FIRST_TIME", true)

        if (isFirstTime) {
            // Perform the data update here

            // For example, you can update a database or perform any other initialization
            getOrders()

            // Set the flag to indicate that the app has been started before
            val editor: SharedPreferences.Editor = prefs.edit()
            editor.putBoolean("PREF_FIRST_TIME", false)
            editor.apply()
        }
        return orderDao.getOrders()
    }

    fun getDistanceIsLessThan50M(
        activity: Activity,
        latitude1: Double,
        longitude1: Double,
        callback: DistanceCallback
    ) {

        if (!isLocationPermissionGranted(activity)) {
            requestLocationPermission(activity)
        } else {
            if (checkInternetConnection(activity)) {
                getUsersCurrentLocation(activity, latitude1, longitude1) {
                    callback.onDistanceCalculated(isWithin50Meters = it)
                }
            } else {
                Toast.makeText(activity, "Please connect to internet!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    interface DistanceCallback {
        fun onDistanceCalculated(isWithin50Meters: Boolean)
    }


    fun getUsersCurrentLocation(
        activity: Activity,
        latitude1: Double,
        longitude1: Double,
        onCalculate: (isLess: Boolean) -> Unit
    ) {
        val locationManager = activity.getSystemService(LOCATION_SERVICE) as LocationManager
        val isGPS = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        Log.println(Log.ASSERT, TAG, "isGPS: $isGPS")
        if (isGPS) {
            val locationListener = LocationListener { location ->
                val latitude2 = location.latitude
                val longitude2 = location.longitude
                println("location2>>>$latitude2,$longitude2")
                onCalculate(calculateDistance(latitude1, latitude2, longitude1, longitude2))
            }

            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestLocationPermission(activity)
                return
            }
            locationManager.requestSingleUpdate(
                LocationManager.NETWORK_PROVIDER,
                locationListener,
                Looper.myLooper()
            )
        }
    }

    fun calculateDistance(
        lat1: Double,
        lat2: Double,
        lon1: Double,
        lon2: Double
    ): Boolean {

        val lon1Rad = Math.toRadians(lon1)
        val lon2Rad = Math.toRadians(lon2)
        val lat1Rad = Math.toRadians(lat1)
        val lat2Rad = Math.toRadians(lat2)

        // Haversine formula
        val dlon = lon2Rad - lon1Rad
        val dlat = lat2Rad - lat1Rad
        val a = sin(dlat / 2).pow(2.0) + cos(lat1Rad) * cos(lat2Rad) * sin(dlon / 2).pow(2.0)
        val c = 2 * asin(sqrt(a))

        // Radius of earth in kilometers. Use 6371 for kilometers
        val radiusOfEarthKm = 6371.0

        // Calculate the distance in kilometers
        val distanceKm = c * radiusOfEarthKm

        // Convert the distance to meters
        val distanceMeters = distanceKm * 1000
        println("distanceMeters $distanceMeters")

        // Check if the distance is within 50 meters
        return distanceMeters <= 50
    }

    fun getTotalCollectedAmtInString(): LiveData<Double>{
        return getOrderDao().getTotalCollectedAmt()
    }

    fun getTotalDelivery(): LiveData<String> {
        val doneLiveData = orderDao.getTotalDeliveryDone()
        val totalLiveData = orderDao.getTotalDelivery()

        val result = MediatorLiveData<String>()

        result.addSource(doneLiveData) { done ->
            val total = totalLiveData.value ?: 0
            result.value = "$done/$total"
        }

        result.addSource(totalLiveData) { total ->
            val done = doneLiveData.value ?: 0
            result.value = "$done/$total"
        }

        return result
    }

}

