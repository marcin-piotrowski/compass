package piotrowski.compass.data

import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Locator
@Inject constructor(
    private val locationProvider: FusedLocationProviderClient
) {
    private var lastKnowLocation: Location? = null

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            locationResult?.locations?.maxByOrNull { it.time }?.let { lastKnowLocation = it }
        }
    }

    @SuppressLint("MissingPermission")
    val locationFlow: Flow<Location> = flow {
        locationProvider.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )

        while (true) {
            delay(LOCATION_REQUEST_FREQUENCY)
            lastKnowLocation?.let { emit(it) }
        }
    }

    fun stop() {
        locationProvider.removeLocationUpdates(locationCallback)
    }

    companion object {
        val locationRequest: LocationRequest = LocationRequest.create().apply {
            interval = LOCATION_REQUEST_FREQUENCY
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        const val LOCATION_REQUEST_FREQUENCY = 1000L
    }
}