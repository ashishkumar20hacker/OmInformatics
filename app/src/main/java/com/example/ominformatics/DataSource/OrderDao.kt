package com.example.ominformatics.DataSource

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface OrderDao {

    @Insert(entity = DbOrderModel::class, onConflict = OnConflictStrategy.REPLACE)
    fun insert(model: DbOrderModel)

    @Delete
    fun delete(model: DbOrderModel)

    @Update
    fun update(model: DbOrderModel)

    @Query("SELECT * FROM OrderData")
    fun getOrders(): LiveData<List<DbOrderModel>>

    @Query("SELECT deliveryStatus FROM OrderData WHERE orderId = :orderId")
    fun getDeliveryStatus(orderId: Int): String

    @Query("SELECT * FROM OrderData WHERE orderId = :orderId")
    suspend fun getOrderById(orderId: Int): DbOrderModel

    @Query("UPDATE OrderData SET deliveryStatus = :status WHERE orderId = :orderId")
    suspend fun updateDeliveryStatus(orderId: Int, status: String)

}