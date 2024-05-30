package com.example.ominformatics.UI

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ominformatics.DataSource.OrderList
import com.example.ominformatics.databinding.ItemOrderBinding

class OrderAdapter(private val context: Context) :
    ListAdapter<OrderList,OrderAdapter.ViewHolder>(diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<OrderList>() {
            override fun areItemsTheSame(oldItem: OrderList, newItem: OrderList): Boolean {
                return oldItem.order_id == newItem.order_id
            }

            override fun areContentsTheSame(oldItem: OrderList, newItem: OrderList): Boolean {
                return oldItem == newItem
            }
        }
    }

}