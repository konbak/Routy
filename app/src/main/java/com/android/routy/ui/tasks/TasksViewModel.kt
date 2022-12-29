package com.android.routy.ui.tasks

import androidx.lifecycle.*
import com.android.routy.data.Task
import com.android.routy.data.TaskDao
import com.android.routy.data.TasksRepository
import com.google.android.libraries.places.api.model.Place
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val taskDao: TaskDao,
    private val repository: TasksRepository,
    state: SavedStateHandle
) : ViewModel(){

    val tasks = repository.getTasks().asLiveData()

    val fabState = state.getLiveData("isOpen", true)

    fun changeFabState(isOpen: Boolean){
        fabState.value = isOpen
    }


    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked, optimize_index = 1000))
    }

    fun saveTask(place : Place) = viewModelScope.launch {
        val newTask = Task(name = place.name!!, address = place.address!!, latitude = place.latLng!!.latitude, longitude = place.latLng!!.longitude)
        taskDao.insert(newTask)
    }


    private val responseMessage = MutableLiveData<String>()
    val msg: LiveData<String> = responseMessage

    fun optimizeRoute(latitude: Double, longitude: Double) = viewModelScope.launch {
        try {
            if(repository.optimizeRoute(latitude, longitude))
                responseMessage.value = "Route optimized"
            else
                responseMessage.value = "Route did not optimized"
        } catch (exception: IOException){
            responseMessage.value = "Route did not optimized: $exception"
        } catch (exception: HttpException){
            responseMessage.value = "Route did not optimized: $exception"
        }
    }
}