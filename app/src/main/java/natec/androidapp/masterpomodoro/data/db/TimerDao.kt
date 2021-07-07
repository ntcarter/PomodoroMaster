package natec.androidapp.masterpomodoro.data.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TimerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimer(item: Timers)

    @Delete
    suspend fun deleteTimer(item: Timers)

    @Update
    suspend fun updateTimer(item: Timers)

    @Query("SELECT * FROM timers")
    fun getAllTimers(): LiveData<List<Timers>>
}