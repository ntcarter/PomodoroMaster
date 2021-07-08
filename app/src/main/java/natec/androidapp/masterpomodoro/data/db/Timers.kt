package natec.androidapp.masterpomodoro.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Creates a timer table to hold different pomodoro timers
 */
@Entity(tableName = "timers")
data class Timers(
    @ColumnInfo(name = "timer_name")
    var name: String,
    // total time is the time of the task set by the user
    @ColumnInfo(name = "task_total_time")
    var totalTime: Int,
    // current time is the amount of time elapsed since the timer started
    @ColumnInfo(name = "task_current_time")
    var curTime: Int,
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
}