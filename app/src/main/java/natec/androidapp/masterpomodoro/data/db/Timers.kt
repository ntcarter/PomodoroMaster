package natec.androidapp.masterpomodoro.data.db

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
    var endTime: Int,
    @ColumnInfo(name = "is_break")
    var isBreak: Boolean = false
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

    @Ignore
    // custom scope that's alive while our class is alive
    val scope = CoroutineScope(Job() + Dispatchers.Main)

    @Ignore
    private var _timeLeft = MutableLiveData(getTaskOrBreakTotalTime() - getTaskOrBreakElapsedTime())
    val timeLeft: LiveData<Int>
        get() = _timeLeft

    @Ignore
    var activejob: Job? = null

    fun scheduleTimer(context: Context) {
        val elapsedTime = getTaskOrBreakElapsedTime()
        val totalTime = getTaskOrBreakTotalTime()

        val alarmManager: AlarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimerBroadcastReceiver::class.java)
        intent.putExtra("ID", id)
        intent.putExtra("NAME", name)
        intent.putExtra("TOTAL_TIME", totalTime)
        intent.putExtra("ELAPSED_TIME", elapsedTime)

        val timerPendingIntent = PendingIntent.getBroadcast(context, id!!, intent, 0)

        val calendar: Calendar = Calendar.getInstance()
        val curTime = System.currentTimeMillis()
        val timerEndTime = curTime + (totalTime - elapsedTime) * 1000

        //set the calendar date to the time from now + our timers time.
        calendar.time = Date(timerEndTime)

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            timerPendingIntent
        )
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
            startTimingTimerTime()
        }
    }

    fun startTimingTimerTime(){
        activejob = scope.launch {
            updateTimerTime()
        }
    }

    private suspend fun updateTimerTime() {
        // loop until timer end or pause on a coroutine thread and update time
        while (isActive && _timeLeft.value!! > 0) {
            delay(1000)
            //get the current time.
            val curTime = System.currentTimeMillis() / 1000

            val timeLeft = (endTime) - curTime
            _timeLeft.value = timeLeft.toInt()
        }
        if(!isBreak) {
            taskElapsedTime = taskTotalTime - _timeLeft.value!!
        }else {
            breakElapsedTime = breakTotalTime - _timeLeft.value!!
        }
        isActive = false
        updateTimer()
    }

    fun pauseTimer() {
        isActive = false
        if(!isBreak){
            taskElapsedTime = taskTotalTime - _timeLeft.value!!
        }else {
            breakElapsedTime = breakTotalTime - _timeLeft.value!!
            // if it is a break and the time left is <=0 then restart the timer
            if(_timeLeft.value!! <= 0){
                restartTimer()
            }
        }
        activejob?.cancel()
        //update the DB with the elapsed time
        updateTimer()
    }

    private fun updateTimer(){
        val db = PomodoroDatabase.invoke(PomodoroApplication.applicationContext())
        val repository = AddTimerRepository(db)
        scope.launch {
            repository.updateTimer(this@Timers)
        }
    }

    fun swapBreakAndTask(){
        isBreak = !isBreak
        // a swap means we start at a new break or task time and need to set time left accordingly
        _timeLeft.value = getTaskOrBreakTotalTime()
    }

    // check to see is Elapsed time >= total time
    // if task isbreak = false and then start break timer automatically by scheduling timer and activating it
    // return true if we started a break and the UI shouldn't change buttons to play
    fun signalStartBreak(): Boolean {
        return getTaskOrBreakElapsedTime() >= getTaskOrBreakTotalTime() &&!isBreak
    }

    fun restartTimer(){
        taskElapsedTime = 0
        breakElapsedTime = 0
        isBreak = false
        _timeLeft.value = getTaskOrBreakTotalTime()
    }

    private fun getTaskOrBreakTotalTime(): Int{
        return if(isBreak){
            breakTotalTime
        }else{
            taskTotalTime
        }
    }

    private fun getTaskOrBreakElapsedTime(): Int{
        return if(isBreak){
            breakElapsedTime
        }else {
            taskElapsedTime
        }
    }
}