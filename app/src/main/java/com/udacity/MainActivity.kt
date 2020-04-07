package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    companion object {
        private const val UDACITY_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide/archive/master.zip"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit/archive/master.zip"

    }

    private var downloadID: Long = 0

    private var URL = ""

    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            val url = getUrlFromRadioButton()
            if (url != null) {
                download(url)
                custom_button.setButtonStatus(ButtonState.Clicked)
            }
        }

        createChannel(
            getString(R.string.channel_id),
            getString(R.string.channel_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            custom_button.setButtonStatus(ButtonState.Loading)

            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.download_complete),
                    Toast.LENGTH_SHORT
                ).show();
                sendDownloadCompleteNotification()
                custom_button.setButtonStatus(ButtonState.Completed)
            }
        }
    }

    private fun download(url: String) {
        URL = url

        Toast.makeText(this, getString(R.string.download_started), Toast.LENGTH_SHORT).show()

        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(getString(R.string.app_name))
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    private fun sendDownloadCompleteNotification() {
        //Notifications
        notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.cancelNotifications()
        notificationManager.sendNotification(URL, this)
    }

    private fun getUrlFromRadioButton(): String? {
        return when (radioGroupFileDownload.checkedRadioButtonId) {
            radioButtonGlide.id -> {
                GLIDE_URL
            }
            radioButtonUdacity.id -> {
                UDACITY_URL
            }
            radioButtonRetrofit.id -> {
                RETROFIT_URL
            }
            else -> {
                Toast.makeText(
                    this,
                    getString(R.string.please_select_file),
                    Toast.LENGTH_SHORT
                ).show()
                null
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_description)
            val notificationManager = this.getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

}
