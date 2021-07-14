package natec.androidapp.masterpomodoro.br

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import natec.androidapp.masterpomodoro.services.TimerService

private const val TAG = "TimerBroadcastR"
class TimerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        Toast.makeText(context, "Alarm Received", Toast.LENGTH_SHORT).show()
        val intentService = Intent(context, TimerService::class.java)
        intentService.putExtra("NAME", intent.getStringExtra("NAME"))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG, "onReceive: startForegroundService O or higher")
            context.startForegroundService(intentService)
        } else {
            Log.d(TAG, "onReceive: startForegroundService ")
            context.startService(intentService)
        }
    }
}