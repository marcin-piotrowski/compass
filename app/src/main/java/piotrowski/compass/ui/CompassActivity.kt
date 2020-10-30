package piotrowski.compass.ui

import android.Manifest
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import dagger.hilt.android.AndroidEntryPoint
import piotrowski.compass.R
import piotrowski.compass.data.Compass
import piotrowski.compass.data.Locator
import piotrowski.compass.databinding.ActivityCompassBinding
import kotlin.math.roundToInt

private val TAG = CompassActivity::class.java.simpleName

@AndroidEntryPoint
class CompassActivity : AppCompatActivity(), DestinationInputDialog.Listener {

    private val viewModel: CompassViewModel by viewModels()
    private val binding: ActivityCompassBinding by lazy {
        ActivityCompassBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            setContentView(root)
            setDestinationButton.setOnClickListener { processLocationPermission() }
        }

        with(viewModel) {
            northAzimuth.observe(this@CompassActivity, ::adjustCompassRose)
            destinationAzimuth.observe(this@CompassActivity, ::adjustDestinationArrow)

            distanceToDestination.observe(this@CompassActivity) {
                binding.distanceText.text = getString(R.string.distance_to, it.roundToInt())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }

    private fun adjustCompassRose(azimuth: Float) {
        val animation = RotateAnimation(
            -viewModel.currentAzimuth, -azimuth,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = Compass.AZIMUTH_REQUEST_FREQUENCY
            repeatCount = 0
            fillAfter = true
        }

        viewModel.currentAzimuth = azimuth

        binding.compassRose.startAnimation(animation)
    }

    private fun adjustDestinationArrow(azimuth: Float) {
        binding.directionArrow.visibility = View.VISIBLE
        val animation = RotateAnimation(
            -viewModel.currentDestinationAzimuth, -azimuth,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = Locator.LOCATION_REQUEST_FREQUENCY
            repeatCount = 0
            fillAfter = true
        }

        viewModel.currentDestinationAzimuth = azimuth

        binding.directionArrow.startAnimation(animation)
    }

    private fun processLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                processLocationSettings()
            }
            else ->
                requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_LOCATION_PERMISSIONS
                )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_LOCATION_PERMISSIONS -> {
                if (
                    (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                )
                    processLocationSettings()
                else
                    AlertDialog.Builder(this)
                        .setMessage(R.string.location_danied_rationale)
                        .setPositiveButton(R.string.ok) { dialog, _ -> dialog.cancel() }
                        .create()
                        .show()

                return
            }
        }
    }

    private fun processLocationSettings() {
        val settingsRequest = LocationSettingsRequest.Builder()
            .addLocationRequest(Locator.locationRequest)
            .build()

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(settingsRequest)
            .apply {
                addOnSuccessListener { openDestinationDialog() }
                addOnFailureListener { exception ->
                    if (exception is ResolvableApiException) {
                        try {
                            exception.startResolutionForResult(
                                this@CompassActivity,
                                REQUEST_CHECK_LOCATION_SETTINGS
                            )
                        } catch (sendEx: IntentSender.SendIntentException) {
                            //no-ops
                        }
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CHECK_LOCATION_SETTINGS -> {
                when (resultCode) {
                    RESULT_OK -> {
                        openDestinationDialog()
                    }
                    else -> {
                        AlertDialog.Builder(this)
                            .setMessage(R.string.location_settings_off_rationale)
                            .setPositiveButton(R.string.ok) { dialog, _ -> dialog.cancel() }
                            .create()
                            .show()
                    }
                }
            }
        }
    }

    private fun openDestinationDialog() {
        DestinationInputDialog(this).show(supportFragmentManager, "DestinationInputDialog")
    }

    override fun onDestinationChoose(location: Location) {
        viewModel.destination = location
    }

    companion object {
        const val REQUEST_CHECK_LOCATION_SETTINGS = 1
        const val REQUEST_LOCATION_PERMISSIONS = 1
    }
}