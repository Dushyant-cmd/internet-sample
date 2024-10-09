package com.services.internetsample.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.services.internetsample.Response
import com.services.internetsample.Success
import com.services.internetsample.data.local.RoomDB
import com.services.internetsample.data.local.entities.InternetSpeedEntity

class MainRepository(private val roomDB: RoomDB) {

    private var speedMLD: MutableLiveData<Response> = MutableLiveData()

    val speedLD: LiveData<Response>
        get() {
            return speedMLD
        }


    fun addInternetSpeed(internetSpeedEntity: InternetSpeedEntity) {
        roomDB.getInternetSpeedDao().insert(internetSpeedEntity)

        speedMLD.value = Success(roomDB.getInternetSpeedDao().getAllInternetSpeeds())
    }

    fun getInternetSpeed() {
        speedMLD.value = Success(roomDB.getInternetSpeedDao().getAllInternetSpeeds())
    }
}