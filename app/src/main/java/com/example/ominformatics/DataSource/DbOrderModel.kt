package com.example.ominformatics.DataSource

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OrderData")
class DbOrderModel (
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "orderId") val order_id: Int,
    @ColumnInfo(name = "orderNo") val order_no: String,
    @ColumnInfo(name = "customerName") val customer_name: String,
    @ColumnInfo(name = "latitude") val latitude: Double,
    @ColumnInfo(name = "longitude") val longitude: Double,
    @ColumnInfo(name = "address") val address: String,
    @ColumnInfo(name = "deliveryCost") val delivery_cost: Double,
    @ColumnInfo(name = "deliveryStatus", defaultValue = "Pending") val delivery_status: String = DeliveryStatus.Pending.status,
    @ColumnInfo(name = "imageUrl") var imageUrl: String = "",
    @ColumnInfo(name = "isDamaged", defaultValue = "false") var isDamaged: String = BoolForDataBase.FALSE.status,
    @ColumnInfo(name = "damageDesc") var damageDesc: String = "",
    @ColumnInfo(name = "anotherAmt") var anotherAmt: Double = 0.0,
)

enum class BoolForDataBase(val status: String) {
    TRUE("true"),
    FALSE("false")
}