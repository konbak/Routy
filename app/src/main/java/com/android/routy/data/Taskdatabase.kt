package com.android.routy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.routy.di.AppModule.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [Task::class], version = 1)
abstract class TaskDatabase : RoomDatabase(){

    abstract fun taskDao(): TaskDao

    class Callback @Inject constructor(
        private val database: Provider<TaskDatabase>,
        @ApplicationScope private val applicationScope: CoroutineScope
    ): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().taskDao()
            applicationScope.launch {
                dao.insert(Task("Home", "Sokratous 12",38.892560390990646, 22.427718300438134, optimize_index = 2 ))
                dao.insert(Task("Home", "Sokratous 12",38.892560390990646, 22.427718300438134, optimize_index = 1 ))
            }
        }
    }
}