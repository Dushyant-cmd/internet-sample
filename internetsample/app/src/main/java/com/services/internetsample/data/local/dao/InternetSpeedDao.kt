package com.services.internetsample.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.services.internetsample.data.local.entities.InternetSpeedEntity

@Dao
interface InternetSpeedDao {

    @Insert
    fun insert(internetSpeed: InternetSpeedEntity)

    @Query("SELECT * FROM internet_speed_table")
    fun getAllInternetSpeeds(): List<InternetSpeedEntity>

    @Query("DELETE FROM internet_speed_table")
    fun deleteAllInternetSpeeds()

    @Update
    fun update(internetSpeed: InternetSpeedEntity)

    @Delete
    fun delete(internetSpeed: InternetSpeedEntity)
}