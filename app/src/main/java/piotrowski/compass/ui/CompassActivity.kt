package piotrowski.compass.ui

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import piotrowski.compass.databinding.ActivityCompassBinding

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
            setDestinationButton.setOnClickListener { openDestinationDialog() }
        }

        viewModel.northAzimuth.observe(this, ::displayNewAzimuth)
    }

    override fun onResume() {
        super.onResume()

        viewModel.startCompass()
    }

    override fun onStop() {
        super.onStop()

        viewModel.stopCompass()
    }

    private fun displayNewAzimuth(azimuth: Float) {
        val animation = RotateAnimation(
            -viewModel.currentAzimuth, -azimuth,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 290
            repeatCount = 0
            fillAfter = true
        }

        viewModel.currentAzimuth = azimuth

        binding.compassNeedle.startAnimation(animation)
    }

    private fun openDestinationDialog() {
        DestinationInputDialog(this).show(supportFragmentManager, "DestinationInputDialog")
    }

    override fun onDestinationSet(latitude: Double, longitude: Double) {

    }
}