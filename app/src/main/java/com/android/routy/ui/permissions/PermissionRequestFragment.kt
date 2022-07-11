package com.android.routy.ui.permissions

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.routy.R
import com.android.routy.databinding.FragmentPermissionRequestBinding
import com.android.routy.util.hasPermission
import com.google.android.material.snackbar.Snackbar


class PermissionRequestFragment : Fragment(R.layout.fragment_permission_request) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPermissionRequestBinding.bind(view)

        binding.apply {
            iconImageView.setImageResource(R.drawable.ic_location_on_24px)
            titleTextView.text = getString(R.string.fine_location_access_rationale_title_text)
            detailsTextView.text = getString(R.string.fine_location_access_rationale_title_text)
            permissionRequestButton.text = getString(R.string.enable_fine_location_button_text)

            permissionRequestButton.setOnClickListener {
                requestLocationPermission()
            }
        }

    }


    private fun requestLocationPermission() {
        val permissionApproved =
            context?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) ?: return

        if (permissionApproved) {
            //go to Tasks fragment
            val action = PermissionRequestFragmentDirections.actionPermissionRequestFragmentToTasksFragment()
            findNavController().navigate(action)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val action =
                PermissionRequestFragmentDirections.actionPermissionRequestFragmentToTasksFragment()
            findNavController().navigate(action)
        } else {
            Snackbar.make(requireView(), "Need location Permission", Snackbar.LENGTH_LONG).show()
        }
    }

}