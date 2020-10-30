package piotrowski.compass.ui

import android.location.Location
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import piotrowski.compass.data.Compass
import piotrowski.compass.data.Locator

private val TAG = CompassViewModel::class.java.simpleName

class CompassViewModel
@ViewModelInject constructor(
    private val compass: Compass,
    private val locator: Locator
) : ViewModel() {
    var currentAzimuth: Float = 0f
    val northAzimuth: LiveData<Float>
        get() = _northAzimuth

    var destination: Location? = null
        set(value) {
            field = value
            if (field == null)
                stopLocator()
            else if (locatorJob == null)
                startLocator()
        }
    val distanceToDestination: LiveData<Float>
        get() = _distanceToDestination

    private val _northAzimuth = MutableLiveData<Float>()
    private val _distanceToDestination = MutableLiveData<Float>()
    private var compassJob: Job? = null
    private var locatorJob: Job? = null

    fun onResume() {
        startCompass()
        if (destination != null) startLocator()
    }

    fun onStop() {
        stopCompass()
        stopLocator()
    }

    private fun startCompass() {
        compassJob = viewModelScope.launch(IO) {
            compass.azimuthFlow.collect { _northAzimuth.postValue(it) }
        }
    }

    private fun stopCompass() {
        compass.stop()
        compassJob?.cancel()
        compassJob = null
    }

    private fun startLocator() {
        locatorJob = viewModelScope.launch(IO) {
            locator.locationFlow.collect {
                _distanceToDestination.postValue(destination?.distanceTo(it))
            }
        }
    }

    private fun stopLocator() {
        locator.stop()
        locatorJob?.cancel()
        locatorJob = null
    }
}