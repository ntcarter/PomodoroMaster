package natec.androidapp.masterpomodoro.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import natec.androidapp.masterpomodoro.R
import natec.androidapp.masterpomodoro.util.Constants.NOTIFICATION_CHANNEL_ID
import natec.androidapp.masterpomodoro.util.Constants.NOTIFICATION_CHANNEL_NAME

private const val TAG = "MyTimerActivity"
@AndroidEntryPoint
class TimerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        Log.d(TAG, "createNotificationChannel: SET UP NOTIFICATION CHANNEL")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}