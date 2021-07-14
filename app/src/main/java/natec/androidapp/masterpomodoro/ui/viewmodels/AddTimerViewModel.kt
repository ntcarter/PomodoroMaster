package natec.androidapp.masterpomodoro.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import natec.androidapp.masterpomodoro.data.db.Timers
import natec.androidapp.masterpomodoro.data.repositories.AddTimerRepository
import natec.androidapp.masterpomodoro.util.convertToSeconds

private const val TAG = "AddTimerViewModel"

class AddTimerViewModel(
    private val repository: AddTimerRepository
) : ViewModel() {

    var activeEditTimer: Timers? = null

    private fun insertTimer(timer: Timers) = viewModelScope.launch {
        repository.insertTimer(timer)
    }

    fun delete(timer: Timers) = viewModelScope.launch {
        repository.deleteTimer(timer)
    }

    private fun updateTimer(timer: Timers) = viewModelScope.launch {
        repository.updateTimer(timer)
    }

    fun getAllTimers() = repository.getAllTimers()

    /**
     * Recieves text attributes from the UI and converts them into the format needed to insert into the database
     */
    fun getTimerReadyForInsert(
        name: String,
        taskH: String,
        taskM: String,
        taskS: String,
        breakH: String,
        breakM: String,
        breakS: String,
        bgColor: Int,
        textColor: Int
    ) {

        val times = getTotalTimes(taskH, taskM, taskS, breakH, breakM, breakS)

        val timerToInsert = Timers(name, times.first, 0, times.second, 0, bgColor, textColor)
        Log.d(TAG, "getTimerReadyForInsert: inserting $timerToInsert ")
        insertTimer(timerToInsert)
    }

    fun getTimerReadyForUpdate(
        name: String,
        taskH: String,
        taskM: String,
        taskS: String,
        breakH: String,
        breakM: String,
        breakS: String,
        bgColor: Int,
        textColor: Int
    ) {
        val times = getTotalTimes(taskH, taskM, taskS, breakH, breakM, breakS)

        // update the values of the current timer and update the DB
        activeEditTimer?.name = name
        activeEditTimer?.taskTotalTime = times.first
        activeEditTimer?.breakTotalTime = times.second
        activeEditTimer?.timerColor = bgColor
        activeEditTimer?.textColor = textColor
        updateTimer(activeEditTimer!!)

        activeEditTimer = null
    }

    private fun getTotalTimes(
        taskH: String,
        taskM: String,
        taskS: String,
        breakH: String,
        breakM: String,
        breakS: String
    ): Pair<Int, Int> {
        //handle empty strings
        val taskHours = if (taskH.isEmpty()) 0 else taskH.toInt()
        val taskMin = if (taskM.isEmpty()) 0 else taskM.toInt()
        val taskSeconds = if (taskS.isEmpty()) 0 else taskS.toInt()

        val breakHours = if (breakH.isEmpty()) 0 else breakH.toInt()
        val breakMin = if (breakM.isEmpty()) 0 else breakM.toInt()
        val breakSeconds = if (breakS.isEmpty()) 0 else breakS.toInt()

        val totalTaskTime = convertToSeconds(taskHours, taskMin, taskSeconds)
        val totalBreakTime = convertToSeconds(breakHours, breakMin, breakSeconds)

        return Pair(totalTaskTime, totalBreakTime)
    }
}