package com.android.routy.ui.tasks

import androidx.lifecycle.*
import com.android.routy.data.Task
import com.android.routy.data.TaskDao
import com.android.routy.data.TasksRepository
import com.google.android.libraries.places.api.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val repository: TasksRepository
) : ViewModel(){

    val tasks = repository.getTasks().asLiveData()

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun saveTask(place : Place) = viewModelScope.launch {
        val newTask = Task(name = place.name!!, address = place.address!!, latitude = place.latLng!!.latitude, longitude = place.latLng!!.longitude)
        taskDao.insert(newTask)
    }

    fun optimizeRoute(latitude: Double, longitude: Double) = viewModelScope.launch {
        repository.optimizeRoute(latitude, longitude)
    }
}