package com.example.ominformatics.UI.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.ominformatics.DataSource.DeliveryStatus
import com.example.ominformatics.DataSource.MyApplication.Companion.getOrderDao
import com.example.ominformatics.UI.adapters.OrderAdapter
import com.example.ominformatics.ViewModel.OrderViewModel
import com.example.ominformatics.ViewModel.OrderViewModel.DistanceCallback
import com.example.ominformatics.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    lateinit var viewOrderModel : OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewOrderModel = ViewModelProvider(this)[OrderViewModel::class.java]

        viewOrderModel.observableOrderList().observe(this){
            val adapter = OrderAdapter(this){
                viewOrderModel.getDistanceIsLessThan50M(this, it.latitude, it.longitude, object: DistanceCallback {
                    override fun onDistanceCalculated(isWithin50Meters: Boolean) {
                        if (isWithin50Meters) {
                            startActivity(Intent(applicationContext, DeliveryActivity::class.java).putExtra("orderId", it.order_id))
                        } else {
                            Toast.makeText(applicationContext, "Delivery location is too far!",Toast.LENGTH_SHORT).show()
                        }
                    }
                })

            }
            binding.orderRv.adapter = adapter
            adapter.submitList(it)
        }

    }
}