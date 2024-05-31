package com.example.ominformatics.DataSource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DbOrderModel::class], version = 1, exportSchema = false)
abstract class OmInformaticsDatabase : RoomDatabase() {

    abstract fun orderDao() : OrderDao

}

object DatabaseBuilder {
    private var INSTANCE: OmInformaticsDatabase? = null
    fun getInstance(context: Context): OmInformaticsDatabase {
        if (INSTANCE == null) {
            synchronized(OmInformaticsDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }
    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            OmInformaticsDatabase::class.java,
            "OmInformatics.db"
        ).fallbackToDestructiveMigration()
            .build()
}