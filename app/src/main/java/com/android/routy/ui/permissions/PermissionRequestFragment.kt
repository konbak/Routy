package com.android.routy.ui.permissions

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.android.routy.R
import com.android.routy.databinding.FragmentPermissionRequestBinding
import com.android.routy.util.hasPermission

private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34


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
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    //go to Tasks fragment
                    val action =
                        PermissionRequestFragmentDirections.actionPermissionRequestFragmentToTasksFragment()
                    findNavController().navigate(action)
                } else {
                    requestLocationPermission()
                    //Snackbar.make(requireView(), "Need location Permission", Snackbar.LENGTH_LONG).show()
                }
                return
            }
        }
    }

}