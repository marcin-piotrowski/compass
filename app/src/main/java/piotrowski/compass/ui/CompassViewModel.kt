package piotrowski.compass.ui

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import piotrowski.compass.data.Compass

private val TAG = CompassViewModel::class.java.simpleName

class CompassViewModel
@ViewModelInject constructor(
    private val compass: Compass
) : ViewModel() {

    var currentAzimuth: Float = 0f

    private val _northAzimuth = MutableLiveData<Float>()
    val northAzimuth: LiveData<Float>
        get() = _northAzimuth

    private var compassJob: Job? = null

    fun startCompass() {
        compassJob = viewModelScope.launch(Dispatchers.IO) {
            compass.azimuthEmittingFrequency = 300
            compass.azimuthFlow
                .collect {
                    _northAzimuth.postValue(it)
                    Log.v(TAG, "1. new azimuth emitted $it")
                }
        }
    }

    fun stopCompass() {
        compassJob?.cancel()
        compassJob = null

        compass.stop()
    }
}