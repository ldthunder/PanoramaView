package com.ldthunder.panoramaview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ldthunder.panorama_view.GyroscopeObserver
import com.ldthunder.panorama_view.GyroscopeObserverDelegate
import com.ldthunder.panorama_view.PanoramaView

class MainActivity : AppCompatActivity() {
    // Lifecycle-aware delegate which automatically register in onResume() and unregister in onPause()
    private val gyroscopeObserver by GyroscopeObserverDelegate(this, GyroscopeObserver())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val panoramaImageView: PanoramaView =
            findViewById<View>(R.id.panorama_image_view) as PanoramaView
        panoramaImageView.setGyroscopeObserver(gyroscopeObserver)
    }

    override fun onResume() {
        super.onResume()
        // Register the GyroscopeObserver
        gyroscopeObserver.setMaxRotateRadian(GyroscopeObserver.SLOW)
        // gyroscopeObserver.register(this)
    }

    override fun onPause() {
        super.onPause()
        gyroscopeObserver.setMaxRotateRadian(GyroscopeObserver.FAST)
        // Unregister the GyroscopeObserver
        // gyroscopeObserver.unregister()
    }
}