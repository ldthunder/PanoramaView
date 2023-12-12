package com.ldthunder.panorama_view

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import java.util.LinkedList
import kotlin.math.abs

/**
 * Created by Iisteel Bekbaev @ldthunder
 */
class GyroscopeObserver : SensorEventListener {
    private var mSensorManager: SensorManager? = null

    // The time in nanoseconds when the last sensor event happened
    private var mLastTimestamp: Long = 0

    // The radians the device already rotate along y-axis and x-axis
    private var mRotateRadianY = 0.0
    private var mRotateRadianX = 0.0

    // The maximum radian that the device should rotate along x and y axis to show image's bounds
    private var mMaxRotateRadian = Math.PI / 9

    // Save position flag
    private var mSavePosition = false

    // List of PanoramaViews that need to be notified every time the device rotates
    private val mViews = LinkedList<PanoramaView>()

    fun addPanoramaImageView(view: PanoramaView?) {
        if (view != null && !mViews.contains(view)) {
            mViews.addFirst(view)
        }
    }

    /**
     * Register the listener. You should call this in
     * [onResume][androidx.appcompat.app.AppCompatActivity.onResume] method of your activity.
     * Example:
     * ```
     * override fun onResume() {
     *         super.onResume()
     *         gyroscopeObserver.register(this)
     *     }
     * ```
     * @see GyroscopeObserver
     * @see GyroscopeObserverDelegate
     */
    fun register(context: Context) {
        if (mSensorManager == null) {
            mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        val gyroscopeSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mSensorManager!!.registerListener(
            this, gyroscopeSensor, SensorManager.SENSOR_DELAY_FASTEST
        )
        if (!mSavePosition){
            mLastTimestamp = 0
            mRotateRadianX = 0.0
            mRotateRadianY = mRotateRadianX
        }
        Log.d(TAG, "Gyroscope is registered")
    }

    /**
     * Unregister the listener. You should call this in
     * [onPause][androidx.appcompat.app.AppCompatActivity.onPause] method of your activity.
     * Example:
     * ```
     * override fun onPause() {
     *         super.onPause()
     *         gyroscopeObserver.unregister()
     *     }
     * ```
     * @see GyroscopeObserver
     * @see GyroscopeObserverDelegate
     */
    fun unregister() {
        if (mSensorManager != null) {
            mSensorManager!!.unregisterListener(this)
            mSensorManager = null
            Log.d(TAG, "Gyroscope is unregistered")
        }
    }

    /**
     * Reacts to the sensor data and adjusts the position of the PanoramaView.
     */
    override fun onSensorChanged(event: SensorEvent) {
        if (mLastTimestamp == 0L) {
            mLastTimestamp = event.timestamp
            return
        }

        val rotateX = abs(event.values[0])
        val rotateY = abs(event.values[1])
        val rotateZ = abs(event.values[2])
        val dT = (event.timestamp - mLastTimestamp) * nanoToSeconds

        if (rotateY > rotateX + rotateZ) {
            mRotateRadianY += (event.values[1] * dT).toDouble()
            if (mRotateRadianY > mMaxRotateRadian) {
                mRotateRadianY = mMaxRotateRadian
            } else if (mRotateRadianY < -mMaxRotateRadian) {
                mRotateRadianY = -mMaxRotateRadian
            } else {
                for (view in mViews) {
                    if (view.orientation == PanoramaView.ORIENTATION_HORIZONTAL) {
                        view.updateProgress((mRotateRadianY / mMaxRotateRadian).toFloat())
                    }
                }
            }
        } else if (rotateX > rotateY + rotateZ) {
            mRotateRadianX += (event.values[0] * dT).toDouble()
            if (mRotateRadianX > mMaxRotateRadian) {
                mRotateRadianX = mMaxRotateRadian
            } else if (mRotateRadianX < -mMaxRotateRadian) {
                mRotateRadianX = -mMaxRotateRadian
            } else {
                for (view in mViews) {
                    if (view.orientation == PanoramaView.ORIENTATION_VERTICAL) {
                        view.updateProgress((mRotateRadianX / mMaxRotateRadian).toFloat())
                    }
                }
            }
        }
        mLastTimestamp = event.timestamp
    }

    /**
     * Sets the maximum angle of rotation for the view.
     * The [Double] value must be between 0 and π/2 (exclusive).
     * @param maxRotateRadian has some constant values:
     * [GyroscopeObserver.SLOW],
     * [GyroscopeObserver.NORMAL] or
     * [GyroscopeObserver.FAST].
     * To set this value manually, divide the PI constant [Math.PI] by any number, e.g. 2.
     */
    fun setMaxRotateRadian(maxRotateRadian: Double) {
        require(!(maxRotateRadian <= 0 || maxRotateRadian > Math.PI / 2)) {
            "There must be the Double value between (0, π/2]."
        }
        mMaxRotateRadian = maxRotateRadian
    }

    /**
     * When [unregister] is called, the current position of [PanoramaView]
     * is saved and then restored when it is registered again.
     * @param save To avoid saving the position of [PanoramaView], set this value to False.
     */
    fun setPositionSaving(save: Boolean){
        mSavePosition = save
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit

    companion object {
        private const val TAG = "PanoramaViewGyroscope"
        // For translation of nanoseconds to seconds
        private const val nanoToSeconds = 1.0f / 1000000000.0f
        // Rotation Speed Constants for MaxRotateRadian
        const val SLOW = Math.PI / 2
        const val NORMAL = Math.PI / 9
        const val FAST = Math.PI / 18
    }
}