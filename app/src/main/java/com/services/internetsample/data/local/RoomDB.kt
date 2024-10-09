package com.services.internetsample.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.DatabaseConfiguration
import androidx.room.InvalidationTracker
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import com.services.internetsample.data.local.dao.InternetSpeedDao
import com.services.internetsample.data.local.entities.InternetSpeedEntity

@Database(entities = [InternetSpeedEntity::class], version = 1, exportSchema = false)
abstract class RoomDB: RoomDatabase() {

    abstract fun getInternetSpeedDao(): InternetSpeedDao

    companion object {
        private const val DATABASE_NAME = "RoomDB"
        private const val DATABASE_VERSION = 1

        @Volatile
        var INSTANCE: RoomDB? = null

        @Synchronized
        fun getInstance(context: Context): RoomDB {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context, RoomDB::class.java, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE!!
        }
    }
}