package com.dbtest.android.utils

import android.app.Activity
import android.content.IntentSender
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.Task


class CreateGPSOnRequest(activity: Activity, requestCode: Int) {
    private  val TAG = "CreateGPSOnRequest"
    init {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
            .setAlwaysShow(true)
            .setNeedBle(true)

        val client: SettingsClient = LocationServices.getSettingsClient(activity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            Log.i(TAG, "Task success")
            try {
                task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                when(e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            (e as ResolvableApiException).apply {
                                startResolutionForResult(activity, requestCode)
                            }
                        } catch (e: IntentSender.SendIntentException) {
                            Log.i(TAG, e.toString())
                        } catch (e: ClassCastException) {
                            Log.i(TAG, e.toString())
                        }
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(TAG, "settings change unavailable")
                }
            }
        }
        task.addOnFailureListener { exception ->
            Log.i(TAG, "Task Failure $exception")
            if (exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(activity,
                        requestCode
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.i(TAG, sendEx.toString())
                }
            }
        }
    }
}