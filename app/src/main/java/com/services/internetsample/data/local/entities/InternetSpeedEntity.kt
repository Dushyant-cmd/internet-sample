package com.services.internetsample.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "internet_speed_table")
data class InternetSpeedEntity(
    @PrimaryKey(autoGenerate = true)
    val primaryKey: Int?,
    val currentSpeed: Long?,
    val timeStamp: Long?,
    val maxSpeed: Long?,
    val minSpeed: Long?
)