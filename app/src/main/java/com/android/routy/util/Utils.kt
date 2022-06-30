package com.android.routy.util

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat


/**
 * Helper functions to simplify permission checks/requests.
 */
fun Context.hasPermission(permission: String): Boolean {
    return ActivityCompat.checkSelfPermission(this, permission) ==
            PackageManager.PERMISSION_GRANTED
}

