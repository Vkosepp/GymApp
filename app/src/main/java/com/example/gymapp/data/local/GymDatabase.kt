package com.example.gymapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gymapp.data.local.dao.GymDao
import com.example.gymapp.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ExerciseEntity::class,
        WorkoutPlanEntity::class,
        PlanExerciseEntity::class,
        PlanSetEntity::class,
        WorkoutSessionEntity::class,
        PerformedSetEntity::class,
        ScheduledWorkoutEntity::class,
        ProgressPhotoEntity::class,
        UserEntity::class // <-- DODANE
    ],
    version = 5, // <-- ZMIANA NA 4
    exportSchema = false
)
abstract class GymDatabase : RoomDatabase() {

    abstract fun gymDao(): GymDao

    companion object {
        @Volatile
        private var INSTANCE: GymDatabase? = null

        fun getDatabase(context: Context): GymDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymDatabase::class.java,
                    "gym_database"
                )
                    .fallbackToDestructiveMigration() // <-- Baza sama się zresetuje przy zmianie wersji
                    .addCallback(GymDatabaseCallback(context))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    // Klasa nasłuchująca momentu utworzenia bazy danych
    private class GymDatabaseCallback(
        private val context: Context
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Uruchamiamy coroutine na wątku IO, żeby nie zablokować interfejsu aplikacji
            CoroutineScope(Dispatchers.IO).launch {
                val dao = getDatabase(context).gymDao()
                dao.insertExercises(InitialExerciseData.exercises)
            }
        }
    }
}