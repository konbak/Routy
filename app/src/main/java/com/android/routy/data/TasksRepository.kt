package com.android.routy.data

import com.android.routy.api.OpenRouteServicesApi
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


class TasksRepository @Inject constructor(
    private val db: TaskDatabase,
    private val api: OpenRouteServicesApi
) {

    private val taskDao = db.taskDao()

    fun getTasks() = taskDao.getTasks()

    suspend fun optimizeRoute(startLat: Double, startLon: Double) {

        val tasksList: List<Task> = taskDao.getTasksForOptimize()

        val tasksArray = JSONArray()

        for(item in tasksList){
            val taskJsonObject = JSONObject()
            taskJsonObject.put("id", item.id)
            taskJsonObject.put("service", 300)
            val locationArray = JSONArray()
            locationArray.put(item.longitude)
            locationArray.put(item.latitude)
            taskJsonObject.put("location", locationArray)

            tasksArray.put(taskJsonObject)
        }

        val vehicleArray = JSONArray()

        val vehicleObject = JSONObject()
        vehicleObject.put("id", 1)
        vehicleObject.put("profile", "driving-car")
        val startLocationArray = JSONArray()
        startLocationArray.put(startLon)
        startLocationArray.put(startLat)
        vehicleObject.put("start", startLocationArray)
        val capacityArray = JSONArray()
        capacityArray.put(tasksList.size)
        vehicleObject.put("capacity", capacityArray)

        vehicleArray.put(vehicleObject)

        val finalJson = JSONObject()
        finalJson.put("jobs", tasksArray)
        finalJson.put("vehicles", vehicleArray)


        val jsonObjectString = finalJson.toString()

        val requestBody = jsonObjectString.toRequestBody("application/json".toMediaTypeOrNull())

        try {
            val response = api.postRouteOptimization(requestBody)
            if(response.isSuccessful){
                val responseJson = JSONObject(response.body()?.string()!!)

                val routeArray = responseJson.getJSONArray("routes")
                val routeObject = routeArray.getJSONObject(0)
                val stepsArray = routeObject.getJSONArray("steps")

                for(i in 0 until stepsArray.length()){
                    val stepsObject = stepsArray.getJSONObject(i)
                    val type = stepsObject.getString("type")
                    if(type.equals("job")){
                        val id = stepsObject.getInt("id")
                        taskDao.updateIndex(i, id)
                    }
                }
            }
        } catch (exception: IOException){

        } catch (exception: HttpException){

        }
    }
}