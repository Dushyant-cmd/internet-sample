package com.services.internetsample.ui.main

import android.app.ActivityManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.services.internetsample.Error
import com.services.internetsample.R
import com.services.internetsample.Restarter
import com.services.internetsample.SpeedService
import com.services.internetsample.Success
import com.services.internetsample.data.local.RoomDB
import com.services.internetsample.data.local.entities.InternetSpeedEntity
import com.services.internetsample.data.repository.MainRepository
import com.services.internetsample.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    var mServiceIntent: Intent? = null
    private var mSpeedService: SpeedService? = null
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    companion object {
        val TAG = "MainActivity.java"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this, MainViewModelFactory(MainRepository(RoomDB.getInstance(this)))).get(MainViewModel::class.java)

        viewModel.getInternetSpeed()
        setObservers()
        mSpeedService = SpeedService()
        mServiceIntent = Intent(this, mSpeedService!!.javaClass)
        if (!isMyServiceRunning(mSpeedService!!.javaClass))
            bindService(mServiceIntent!!, object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    val binder = service as SpeedService.LocalBinder
                    val speedService = binder.getService()
                    speedService.displaySpeed()
                }

                override fun onServiceDisconnected(name: ComponentName?) {}
            }, BIND_AUTO_CREATE)
    }

    private fun setObservers() {
        viewModel.speedLD.observe(this) {
            when(it) {
                is Success<*> -> {
                    var min: Long = 0
                    var max: Long = 0
                    var mean: Long = 0
                    for((i, e) in (it.data as List<*>).withIndex()) {
                        if(i == 0) {
                            min = (e as InternetSpeedEntity).minSpeed!!
                            max = e.maxSpeed!!
                        }
                        if(i == it.data.size - 1) {
                            binding.currentTv.text = "Current Speed: ${(e as InternetSpeedEntity).currentSpeed.toString()}"
                        }

                        val item = e as InternetSpeedEntity
                        if(item.maxSpeed!! > max)
                            max = item.maxSpeed

                        if(item.minSpeed!! < min)
                            min = item.minSpeed
                    }

                    mean = (max + min) / 2

                    binding.meanTv.text = "Mean Speed: ${mean}"
                    binding.minTv.text = "Min Speed: ${min}"
                    binding.maxTv.text = "Max Speed: ${max}"
                }

                is Error<*> -> {
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }


    override fun onDestroy() {
        val broadcastIntent = Intent()
        broadcastIntent.setAction("restartservice")
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
        super.onDestroy()
    }
}