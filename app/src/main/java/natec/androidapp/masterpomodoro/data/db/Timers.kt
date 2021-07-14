package natec.androidapp.masterpomodoro.data.db

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import natec.androidapp.masterpomodoro.br.TimerBroadcastReceiver
import natec.androidapp.masterpomodoro.services.TimerService
import java.util.*

const val TAG = "Timers"

/**
 * Creates a timer table to hold different pomodoro timers
 */
@Entity(tableName = "timers")
data class Timers(
    @ColumnInfo(name = "timer_name")
    var name: String,
    // total time is the time of the task set by the user in seconds
    @ColumnInfo(name = "task_total_time")
    var taskTotalTime: Int,
    // current time is the amount of time elapsed since the timer started in seconds
    @ColumnInfo(name = "task_current_time")
    var taskCurTime: Int,
    @ColumnInfo(name = "break_total_time")
    var breakTotalTime: Int,
    @ColumnInfo(name = "break_current_time")
    var breakCurTime: Int,
    @ColumnInfo(name = "timer_color")
    var timerColor: Int,
    @ColumnInfo(name = "text_color")
    var textColor: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    fun scheduleTimer(context: Context) {
        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimerBroadcastReceiver::class.java)
        intent.putExtra("ID", id)
        intent.putExtra("NAME", name)
        intent.putExtra("TASK_TOTAL_TIME", taskTotalTime)
        intent.putExtra("BREAK_TOTAL_TIME", breakTotalTime)

        val timerPendingIntent = PendingIntent.getBroadcast(context, id!!, intent, 0)

        val calendar: Calendar = Calendar.getInstance()
        val curTime = System.currentTimeMillis()

        //set the calendar date to the time from now + our timers time.
        calendar.time = Date(curTime + taskTotalTime * 1000)

        Log.d(TAG, "scheduleTimer: ${calendar.time}")

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            timerPendingIntent
        )

        Toast.makeText(context, "Alarm scheduled for $id", Toast.LENGTH_SHORT).show()
    }

    fun cancelTimer(context: Context){
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimerBroadcastReceiver::class.java)
        val timerPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, id!!, intent, 0)
        alarmManager.cancel(timerPendingIntent)

        val serviceIntent = Intent(context, TimerService::class.java)
        context.stopService(serviceIntent)
        Toast.makeText(context, "Alarm cancelled for $id", Toast.LENGTH_SHORT).show()
    }
}