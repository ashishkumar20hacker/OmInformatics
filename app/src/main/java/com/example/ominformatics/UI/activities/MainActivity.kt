package com.example.ominformatics.UI.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ominformatics.ViewModel.OrderViewModel
import com.example.ominformatics.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    lateinit var binding: ActivityMainBinding

    lateinit var viewOrderModel : OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        viewOrderModel = ViewModelP

    }
}