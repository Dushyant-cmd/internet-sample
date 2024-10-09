package com.services.internetsample

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.services.internetsample.data.local.RoomDB
import com.services.internetsample.data.local.entities.InternetSpeedEntity
import com.services.internetsample.ui.main.MainActivity
import java.util.Timer
import java.util.TimerTask


class SpeedService : Service() {
    private val binder: LocalBinder = LocalBinder()
    private var cm: ConnectivityManager? = null

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) startMyOwnForeground()
        else startForeground(1, Notification())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "internet_sample"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )

        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?)!!
        manager.createNotificationChannel(chan)

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)

        val notification = notificationBuilder
            .setOngoing(true)
            .setContentTitle("Speed")
            .setPriority(NotificationManager.IMPORTANCE_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()

        startForeground(2, notification)
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        //Because of foreground service onStartCommand callback is called.
        displaySpeed()

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        Log.d(MainActivity.TAG, "onTaskRemoved: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        stopDisplaySpeed()

        val broadcastIntent = Intent()
        broadcastIntent.setAction("restartservice")
        broadcastIntent.setClass(this, Restarter::class.java)
        this.sendBroadcast(broadcastIntent)
    }

    fun displaySpeed() {
        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                cm?.let {
                    val network = it.activeNetworkInfo
                    val nc = it.getNetworkCapabilities(it.activeNetwork)
                    val upMB = nc?.linkUpstreamBandwidthKbps?.div(1000)?.toLong()
                    val downMB = nc?.linkDownstreamBandwidthKbps?.div(1000)?.toLong()

                    val entity = InternetSpeedEntity(null, upMB, System.currentTimeMillis(), downMB, upMB)

                    val roomDB = RoomDB.getInstance(this@SpeedService)
                    roomDB.getInternetSpeedDao().insert(entity)

                    val NOTIFICATION_CHANNEL_ID = "internet_sample"
                    val channelName = "Background Service"

                    val notificationBuilder: NotificationCompat.Builder =
                        NotificationCompat.Builder(this@SpeedService, NOTIFICATION_CHANNEL_ID)

                    val notification = notificationBuilder
                        .setOngoing(true)
                        .setContentTitle("Speed")
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .build()

                    with(NotificationManagerCompat.from(this@SpeedService)) {
                        if(checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED)
                            return@with

                        notify(101, notification)
                    }
                }
            }
        }

        timer!!.schedule(timerTask, 10000, 10000)
    }

    private fun stopDisplaySpeed() {
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): SpeedService {
            return this@SpeedService
        }
    }
}