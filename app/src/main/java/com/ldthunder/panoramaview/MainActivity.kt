package com.ldthunder.panoramaview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ldthunder.panorama_view.GyroscopeObserver
import com.ldthunder.panorama_view.PanoramaView

class MainActivity : AppCompatActivity() {
    private lateinit var gyroscopeObserver: GyroscopeObserver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val panoramaImageView: PanoramaView =
            findViewById<View>(R.id.panorama_image_view) as PanoramaView

        gyroscopeObserver = GyroscopeObserver().also {
            it.setMaxRotateRadian(GyroscopeObserver.NORMAL)
            panoramaImageView.setGyroscopeObserver(it)
        }

    }

    override fun onResume() {
        super.onResume()
        // Register GyroscopeObserver.
        gyroscopeObserver.register(this)
    }

    override fun onPause() {
        super.onPause()
        // Unregister GyroscopeObserver.
        gyroscopeObserver.unregister()
    }
}