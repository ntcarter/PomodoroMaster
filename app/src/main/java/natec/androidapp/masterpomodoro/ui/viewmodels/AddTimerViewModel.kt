package natec.androidapp.masterpomodoro.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import natec.androidapp.masterpomodoro.data.db.Timers
import natec.androidapp.masterpomodoro.data.repositories.AddTimerRepository

private const val TAG = "AddTimerViewModel"

class AddTimerViewModel(
    private val repository: AddTimerRepository
) : ViewModel() {

    var activeEditTimer: Timers? = null

    private fun insertTimer(timer: Timers) = CoroutineScope(Dispatchers.Main).launch {
        repository.insertTimer(timer)
    }

    fun delete(timer: Timers) = CoroutineScope(Dispatchers.Main).launch {
        repository.deleteTimer(timer)
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
        breakS: String
    ) {
        var totalTaskTime = 0
        // time values stored in seconds need to convert from hours/minutes
        if(taskH.isNotEmpty()){
            totalTaskTime += taskH.toInt()*360
        }
        if(taskM.isNotEmpty()){
            totalTaskTime += taskM.toInt()*60
        }
        if(taskS.isNotEmpty()){
            totalTaskTime += taskS.toInt()
        }

        var totalBreakTime = 0
        if(breakH.isNotEmpty()){
            totalBreakTime += breakH.toInt()*360
        }
        if(breakM.isNotEmpty()){
            totalBreakTime += breakM.toInt()*60
        }
        if(breakS.isNotEmpty()){
            totalBreakTime += breakS.toInt()
        }

        val timerToInsert = Timers(name, totalTaskTime, 0, totalBreakTime, 0, "FFFFFF", "000000")
        Log.d(TAG, "getTimerReadyForInsert: inserting $timerToInsert ")
        insertTimer(timerToInsert)
    }
}