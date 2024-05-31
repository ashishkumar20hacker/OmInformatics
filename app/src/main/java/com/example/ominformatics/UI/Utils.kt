package com.example.ominformatics.UI

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import androidx.core.content.ContextCompat


object Utils {

    private val LOCATION_PERMISSION_REQ_CODE = 123
    private val STORAGE_PERMISSION_REQ_CODE = 124
    private val CAMERA_PERMISSION_REQ_CODE = 125

    fun isLocationPermissionGranted(activity: Activity?): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            activity, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestLocationPermission(activity: Activity) {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(
                arrayOf<String>(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), LOCATION_PERMISSION_REQ_CODE
            )
        }
    }

    fun isCameraPermissionGranted(activity: Activity?): Boolean {
        return ContextCompat.checkSelfPermission(
            activity!!,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestCameraPermission(activity: Activity) {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(
                arrayOf<String>(
                    Manifest.permission.CAMERA
                ), CAMERA_PERMISSION_REQ_CODE
            )
        }
    }

    fun isStoragePermissionGranted(activity: Activity?): Boolean {
        return if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                activity!!, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestStoragePermission(activity: Activity) {
        if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.requestPermissions(
                arrayOf<String>(Manifest.permission.READ_MEDIA_IMAGES),
                STORAGE_PERMISSION_REQ_CODE
            )
        } else {
            if (SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(
                    arrayOf<String>(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), STORAGE_PERMISSION_REQ_CODE
                )
            }
        }
    }

    fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)!!
            .state == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)!!.state == NetworkInfo.State.CONNECTED
    }
}