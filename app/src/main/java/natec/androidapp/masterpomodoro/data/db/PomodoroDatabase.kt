package natec.androidapp.masterpomodoro.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Timers::class],
    version = 1
)
abstract class PomodoroDatabase : RoomDatabase() {

    abstract fun getTimerDao(): TimerDao

    companion object {
        // creates a singleton on this shoppingDatabase
        @Volatile // makes this visible to other threads so only one thread takes action on this database at a time
        private var instance: PomodoroDatabase? = null
        private val LOCK = Any()

        // returns an instance of our database, if there is none we use synchronized to make sure no
        // other threads execute on our database while our thread creates a database to return
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                PomodoroDatabase::class.java,
                "PomodoroTimers.db"
            ).build()
    }
}