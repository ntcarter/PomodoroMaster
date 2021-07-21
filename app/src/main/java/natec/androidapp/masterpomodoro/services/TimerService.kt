package natec.androidapp.masterpomodoro.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.core.app.NotificationCompat
import natec.androidapp.masterpomodoro.R
import natec.androidapp.masterpomodoro.ui.TimerActivity
import natec.androidapp.masterpomodoro.util.Constants
import natec.androidapp.masterpomodoro.util.Constants.NOTIFICATION_CHANNEL_ID

private const val TAG = "TimerService"
class TimerService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator

    override fun onCreate() {
        super.onCreate()

        mediaPlayer = MediaPlayer.create(applicationContext, R.raw.musician)
        mediaPlayer.isLooping = true
        Log.d(TAG, "onCreate: The timerService has been created")
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onCreate: The timerService has been started")

        val notificationIntent = Intent(this, TimerActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val alarmTitle = String.format("%s Alarm", intent!!.getStringExtra("NAME"))

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(Constants.NOTIFICATION_ID)

        val notification: Notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(alarmTitle)
            .setContentText("Ring Ring .. Ring Ring")
            .setSmallIcon(R.drawable.timer_notif)
            .setContentIntent(pendingIntent)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()

        mediaPlayer.start()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mVibratePattern =  longArrayOf(0, 600, 400, 600)
            vibrator.vibrate(VibrationEffect.createWaveform(mVibratePattern, 0));
        } else {
            //deprecated in API 26
            val pattern = longArrayOf(0, 100, 1000)
            vibrator.vibrate(pattern, 0)
        }
        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onCreate: The timerService has been destroyed")
        mediaPlayer.stop()
        vibrator.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}