package com.android.routy.ui.tasks

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.routy.BuildConfig
import com.android.routy.R
import com.android.routy.data.Task
import com.android.routy.databinding.FragmentTasksBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TaskAdapter.OnItemClickListener {

    private val viewModel: TasksViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val AUTOCOMPLETE_REQUEST_CODE = 1

    private var currentLocation: Location? = null

    lateinit var fabAddNew: FloatingActionButton
    lateinit var fabSelectExisted: FloatingActionButton

    private val callback = OnMapReadyCallback { googleMap ->

        googleMap.setMyLocationEnabled(true)

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                if (location != null) {
                    currentLocation = location
                }
            }


        viewModel.tasks.observe(viewLifecycleOwner){ tasksList ->
            googleMap.clear()
            var minLat = Int.MAX_VALUE.toDouble()
            var maxLat = Int.MIN_VALUE.toDouble()
            var minLon = Int.MAX_VALUE.toDouble()
            var maxLon = Int.MIN_VALUE.toDouble()
            for (item in tasksList){
                val marker = LatLng(item.latitude, item.longitude)
                var index = ""
                if(item.optimize_index < 1000)
                    index = item.optimize_index.toString()

                googleMap.addMarker(MarkerOptions().position(marker).title(index+" "+item.name))

                maxLat = Math.max(item.latitude, maxLat)
                minLat = Math.min(item.latitude, minLat)
                maxLon = Math.max(item.longitude, maxLon)
                minLon = Math.min(item.longitude, minLon)
            }

            val bounds = LatLngBounds.Builder().include(LatLng(maxLat, maxLon))
                .include(LatLng(minLat, minLon)).build()
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        // Initialize the SDK
        if(!Places.isInitialized()) {
            Places.initialize(requireContext(), BuildConfig.MAPS_API_KEY)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        val binding = FragmentTasksBinding.bind(view)

        fabSelectExisted = binding.fabSelectExisted
        fabAddNew = binding.fabAddNew

        val taskAdapter = TaskAdapter(this)

        binding.apply {
            recyclerViewTasks.apply {
                adapter = taskAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            fabAddTasks.setOnClickListener{
                if(viewModel.fabState.value == true){
                    viewModel.changeFabState(false)
                }else{
                    viewModel.changeFabState(true)
                }
            }

            fabAddNew.setOnClickListener{
                val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(requireContext())
                startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
            }

            optimizeButton.setOnClickListener{
                if(currentLocation != null) {
                    viewModel.optimizeRoute(currentLocation!!.latitude, currentLocation!!.longitude)
                }
            }

            viewModel.msg.observe(viewLifecycleOwner){
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
            }
        }


        viewModel.fabState.observe(viewLifecycleOwner){
            if(it){
                fabAddNew.animate().translationY(0F)
                fabSelectExisted.animate().translationY(0F)
            }else{
                fabAddNew.animate().translationY(-200F)
                fabSelectExisted.animate().translationY(-400F)
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner){ tasksList ->
            taskAdapter.submitList(tasksList)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        viewModel.saveTask(place)
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.i("TAG", status.statusMessage ?: "")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onCheckBoxClick(task: Task, isChecked: Boolean) {
        viewModel.onTaskCheckedChanged(task, isChecked)
    }
}