package com.android.routy.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "task_table")
@Parcelize
data class Task(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val optimize_index: Int = 1000,
    val completed: Boolean = false,
    @PrimaryKey(autoGenerate = true)
val id: Int = 0
) : Parcelable
