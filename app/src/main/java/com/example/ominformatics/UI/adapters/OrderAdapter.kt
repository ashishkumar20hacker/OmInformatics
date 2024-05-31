package com.example.ominformatics.UI.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ominformatics.DataSource.DbOrderModel
import com.example.ominformatics.databinding.ItemOrderBinding

class OrderAdapter(private val context: Context, val onItemClick: (orderId: DbOrderModel) -> Unit) :
    ListAdapter<DbOrderModel, OrderAdapter.ViewHolder>(diffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderModel = getItem(position) ?: return

        holder.binding.orderNoTv.text = orderModel.order_no
        holder.binding.customerNameTv.text = orderModel.customer_name
        holder.binding.deliveryStatusTv.text = orderModel.delivery_status
        holder.binding.amtTv.text = "₹${orderModel.delivery_cost}"

        holder.itemView.setOnClickListener {
            onItemClick(orderModel)
        }
    }

    inner class ViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<DbOrderModel>() {
            override fun areItemsTheSame(oldItem: DbOrderModel, newItem: DbOrderModel): Boolean {
                return oldItem.order_id == newItem.order_id
            }

            override fun areContentsTheSame(oldItem: DbOrderModel, newItem: DbOrderModel): Boolean {
                return oldItem.delivery_status == newItem.delivery_status
            }
        }
    }

}