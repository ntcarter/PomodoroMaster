package natec.androidapp.masterpomodoro.br

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import natec.androidapp.masterpomodoro.services.TimerService
import natec.androidapp.masterpomodoro.util.Constants

private const val TAG = "TimerBroadcastR"
class TimerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val intentService = Intent(context, TimerService::class.java)
        intentService.putExtra(Constants.INTENT_NAME, intent.getStringExtra(Constants.INTENT_NAME))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService)
        } else {
            context.startService(intentService)
        }
    }
}