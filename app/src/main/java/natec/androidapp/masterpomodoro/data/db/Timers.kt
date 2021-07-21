package natec.androidapp.masterpomodoro.data.db

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.coroutines.*
import natec.androidapp.masterpomodoro.PomodoroApplication
import natec.androidapp.masterpomodoro.br.TimerBroadcastReceiver
import natec.androidapp.masterpomodoro.data.repositories.AddTimerRepository
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
    @ColumnInfo(name = "task_elapsed_time")
    var taskElapsedTime: Int,
    @ColumnInfo(name = "break_total_time")
    var breakTotalTime: Int,
    @ColumnInfo(name = "break_elapsed_time")
    var breakElapsedTime: Int,
    @ColumnInfo(name = "timer_color")
    var timerColor: Int,
    @ColumnInfo(name = "text_color")
    var textColor: Int,
    @ColumnInfo(name = "is_currently_active")
    var isActive: Boolean,
    @ColumnInfo(name = "timer_end_time")
    var endTime: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @Ignore
    // custom scope that's alive while our class is alive
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    @Ignore
    private var _taskTimeLeft = MutableLiveData(taskTotalTime - taskElapsedTime)
    val taskTimeLeft: LiveData<Int>
        get() = _taskTimeLeft

    @Ignore
    var activejob: Job? = null

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
        val timerEndTime = curTime + (taskTotalTime - taskElapsedTime) * 1000

        //set the calendar date to the time from now + our timers time.
        calendar.time = Date(timerEndTime)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            timerPendingIntent
        )

        Log.d(TAG, "scheduleTimer ALARM SCHEDULED FOR: ${calendar.time}")
        Log.d(TAG, "scheduleTimer: ENDTIME: ${timerEndTime / 1000}")
        endTime = (timerEndTime / 1000).toInt()
    }

    fun cancelTimer(context: Context) {
        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimerBroadcastReceiver::class.java)
        val timerPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, id!!, intent, 0)
        alarmManager.cancel(timerPendingIntent)

        val serviceIntent = Intent(context, TimerService::class.java)
        context.stopService(serviceIntent)
    }

    fun activateTimer() {
        if (!isActive) {
            isActive = true
            updateTimer() // update the DB with this timer being active and its scheduled end time
            Log.d(TAG, "activateTimer: LAUNCHING COROUTINE")
            startTimingTimerTime()
        }
    }

    fun startTimingTimerTime(){
        activejob = scope.launch {
            updateTimerTime()
        }
    }

    private suspend fun updateTimerTime() {
        // loop infinitely on a coroutine thread and update time
        while (isActive && _taskTimeLeft.value!! > 0) {
            delay(1000)
            //get the current time.
            val curTime = System.currentTimeMillis() / 1000

            val timeLeft = (endTime) - curTime
            Log.d(TAG, "TIMELEFT: $timeLeft ")
            Log.d(TAG, "3: ----------------------------------------------")
            _taskTimeLeft.value = timeLeft.toInt()
        }
        taskElapsedTime = taskTotalTime - _taskTimeLeft.value!!
        Log.d(TAG, "updateUI: Elapsed time: $taskElapsedTime")
        isActive = false
        updateTimer()
    }

    fun pauseTimer() {
        Log.d(TAG, "pauseTimer: PAUSING TIMER")
        isActive = false
        taskElapsedTime = taskTotalTime - _taskTimeLeft.value!!
        activejob?.cancel()
        //update the DB with the elapsed time
        updateTimer()
    }

    private fun updateTimer(){
        Log.d(TAG, "updateTimer: UPDATING ELAPSED TIME WITH: ${this.taskElapsedTime}")

        val db = PomodoroDatabase.invoke(PomodoroApplication.applicationContext())
        val repository = AddTimerRepository(db)
        Log.d(TAG, "REPOSITORY: $repository")
        scope.launch {
            repository.updateTimer(this@Timers)
        }
    }

    fun restartTimer(){
        taskElapsedTime = 0
        _taskTimeLeft.value = taskTotalTime
    }
}