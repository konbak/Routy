package com.android.routy.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM task_table WHERE (completed = 0) ORDER BY optimize_index, id")
    fun getTasks(): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE completed = 0")
    suspend fun getTasksForOptimize(): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Query("UPDATE task_table SET optimize_index = :index WHERE id = :id")
    suspend fun updateIndex(index: Int, id: Int)
}