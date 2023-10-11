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
 * Created by @ldthunder
 */
class GyroscopeObserver : SensorEventListener {
    private var mSensorManager: SensorManager? = null

    // The time in nanosecond when last sensor event happened.
    private var mLastTimestamp: Long = 0

    // The radian the device already rotate along y-axis.
    private var mRotateRadianY = 0.0

    // The radian the device already rotate along x-axis.
    private var mRotateRadianX = 0.0

/**
 * The maximum radian that the device should rotate
 * along x-axis and y-axis to show image's bounds
 * The value must be between (0, π/2].
 */
    private var mMaxRotateRadian = Math.PI / 9

    // The PanoramaImageViews to be notified when the device rotate.
    private val mViews = LinkedList<PanoramaView>()
    fun register(context: Context) {
        if (mSensorManager == null) {
            mSensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }
        val mSensor = mSensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mSensorManager!!.registerListener(
            this, mSensor, SensorManager.SENSOR_DELAY_FASTEST
        )
        mLastTimestamp = 0
        mRotateRadianX = 0.0
        mRotateRadianY = mRotateRadianX
    }

    fun unregister() {
        if (mSensorManager != null) {
            mSensorManager!!.unregisterListener(this)
            mSensorManager = null
        }
    }

    fun addPanoramaImageView(view: PanoramaView?) {
        if (view != null && !mViews.contains(view)) {
            mViews.addFirst(view)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (mLastTimestamp == 0L) {
            mLastTimestamp = event.timestamp
            return
        }
        val rotateX = abs(event.values[0])
        val rotateY = abs(event.values[1])
        val rotateZ = abs(event.values[2])
        if (rotateY > rotateX + rotateZ) {
            val dT = (event.timestamp - mLastTimestamp) * NS2S
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
            val dT = (event.timestamp - mLastTimestamp) * NS2S
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
        Log.d("GYROSCOPE_VALUES", "${event.values[0]}, ${event.values[1]}, ${event.values[2]}")
        mLastTimestamp = event.timestamp
    }

    /**
     * TODO: Write the documentation with @link and @param
     */
    fun setMaxRotateRadian(maxRotateRadian: Double) {
        require(!(maxRotateRadian <= 0 || maxRotateRadian > Math.PI / 2)) {
            "There must be the Double value between (0, π/2]." }
        mMaxRotateRadian = maxRotateRadian
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Do some logic here")
    }

    companion object {
        // For translate nanosecond to second.
        private const val NS2S = 1.0f / 1000000000.0f
        // Rotation Speed Constants for MaxRotateRadian
        const val NORMAL = Math.PI / 9
        const val FAST = Math.PI / 4
        const val SLOW = Math.PI / 18
    }
}

//override fun onSensorChanged(event: SensorEvent) {
//    if (mLastTimestamp == 0L) {
//        mLastTimestamp = event.timestamp
//        return
//    }
//    val rotateX = abs(event.values[0])
//    val rotateY = abs(event.values[1])
//    val rotateZ = abs(event.values[2])
//    if (rotateY > rotateX + rotateZ) {
//        val dT = (event.timestamp - mLastTimestamp) * GyroscopeObserver.NS2S
//        mRotateRadianY += (event.values[1] * dT).toDouble()
//        if (mRotateRadianY > mMaxRotateRadian) {
//            mRotateRadianY = mMaxRotateRadian
//        } else if (mRotateRadianY < -mMaxRotateRadian) {
//            mRotateRadianY = -mMaxRotateRadian
//        } else {
//            for (view in mViews) {
//                if (view != null && view.orientation == PanoramaView.ORIENTATION_HORIZONTAL) {
//                    view.updateProgress((mRotateRadianY / mMaxRotateRadian).toFloat())
//                }
//            }
//        }
//    } else if (rotateX > rotateY + rotateZ) {
//        val dT = (event.timestamp - mLastTimestamp) * GyroscopeObserver.NS2S
//        mRotateRadianX += (event.values[0] * dT).toDouble()
//        if (mRotateRadianX > mMaxRotateRadian) {
//            mRotateRadianX = mMaxRotateRadian
//        } else if (mRotateRadianX < -mMaxRotateRadian) {
//            mRotateRadianX = -mMaxRotateRadian
//        } else {
//            for (view in mViews) {
//                if (view != null && view.orientation == PanoramaView.ORIENTATION_VERTICAL) {
//                    view.updateProgress((mRotateRadianX / mMaxRotateRadian).toFloat())
//                }
//            }
//        }
//    }
//    Log.d("DAN", "${event.values[0]}, ${event.values[1]}, ${event.values[2]}")
//    mLastTimestamp = event.timestamp
//}
