package natec.androidapp.masterpomodoro.data.repositories

import natec.androidapp.masterpomodoro.data.db.PomodoroDatabase
import natec.androidapp.masterpomodoro.data.db.Timers
import javax.inject.Inject

class AddTimerRepository @Inject constructor(
    private var db: PomodoroDatabase
) {
    suspend fun insertTimer(timer: Timers) = db.getTimerDao().insertTimer(timer)
    suspend fun deleteTimer(timer: Timers) = db.getTimerDao().deleteTimer(timer)
    suspend fun updateTimer(timer: Timers) = db.getTimerDao().updateTimer(timer)

    fun getAllTimers() = db.getTimerDao().getAllTimers()
}