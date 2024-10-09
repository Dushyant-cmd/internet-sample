package com.services.internetsample.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.services.internetsample.Response
import com.services.internetsample.data.local.RoomDB
import com.services.internetsample.data.local.entities.InternetSpeedEntity
import com.services.internetsample.data.repository.MainRepository

class MainViewModel(private val mainRepo: MainRepository) : ViewModel() {

    val speedLD: LiveData<Response>
        get() {
            return mainRepo.speedLD
        }

    fun addInternetSpeed(internetSpeedEntity: InternetSpeedEntity) {
        mainRepo.addInternetSpeed(internetSpeedEntity)
    }

    fun getInternetSpeed() {
        mainRepo.getInternetSpeed()
    }
}

class MainViewModelFactory(val mainRepo: MainRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(mainRepo) as T
    }

}