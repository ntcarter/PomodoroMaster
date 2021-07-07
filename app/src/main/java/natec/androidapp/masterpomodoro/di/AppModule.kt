package natec.androidapp.masterpomodoro.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import natec.androidapp.masterpomodoro.data.db.PomodoroDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideTimerDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        PomodoroDatabase::class.java,
        "PomodoroTimers.db"
    ).build()

}