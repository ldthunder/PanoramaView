package com.ldthunder.panoramaview

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.ldthunder.panorama_view.GyroscopeObserver
import com.ldthunder.panorama_view.GyroscopeObserverDelegate
import com.ldthunder.panorama_view.PanoramaView

class MainActivity : AppCompatActivity() {
    /** Lifecycle-aware delegate that automatically registers a listener
     *  in [onResume][androidx.appcompat.app.AppCompatActivity.onResume]
     *  and unregisters it in [onPause][androidx.appcompat.app.AppCompatActivity.onPause]. */
    private val gyroscopeObserver by GyroscopeObserverDelegate(this, GyroscopeObserver())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val panoramaImageView = findViewById<View>(R.id.panorama_image_view) as PanoramaView
        // Adding Gyroscope Observer to the view
        panoramaImageView.setGyroscopeObserver(gyroscopeObserver)
        // Changing the maximum rotation radian
        gyroscopeObserver.setMaxRotateRadian(GyroscopeObserver.SLOW)
        // Saving the position of view when you unregister the observer
        gyroscopeObserver.setPositionSaving(true)

        panoramaImageView.setOnPanoramaScrollListener(object : PanoramaView.OnPanoramaScrollListener {
            override fun onScrolled(view: PanoramaView?, offsetProgress: Float) {
                /* The offsetProgress range is from -1 to 1, indicating the scrolling of the image
                from left(top) to right(bottom) */
            }
        })
    }

    override fun onResume() {
        super.onResume()
        // gyroscopeObserver.register(this) - if you don't want to use the delegate
    }

    override fun onPause() {
        super.onPause()
        // gyroscopeObserver.unregister() - if you don't want to use the delegate
    }
}