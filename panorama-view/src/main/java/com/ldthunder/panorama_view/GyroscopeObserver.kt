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
    // Nullable instance of SensorManager ру
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
     * НОВОЕ: ЧТОБЫ МЕНЯТЬ СКОРОСТЬ ЧЕРЕЗ API, НАДО ИЗМЕНЯТЬ
     * ЭТОТ ПАРАМЕТР ЧЕРЕЗ 4 КОНСТАНТЫ(ОБЪЯВИМ ИХ ТУТ ЖЕ)
     * ИЗМЕНЕНИЕ ДОЛЖНА ЗАНИМАТЬСЯ ОУПЕН ФУНКЦИЯ changeRotationSpeed()
     */
    private var mMaxRotateRadian = Math.PI / 9

    // The list of PanoramaImageViews to be notified when the device is rotating.
    private val mViews = LinkedList<PanoramaView>()

    /**
     * Определяет SensorManager и регистрирует Listener на Gyroscope Sensor.
     * Обновляет все данные положения панорамы.
     */
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

    // Отзывает регистрацию и очищает Sensor Manager instance
    fun unregister() {
        if (mSensorManager != null) {
            mSensorManager!!.unregisterListener(this)
            mSensorManager = null
        }
    }

    // Adds the view into the list of Views Ру
    fun addPanoramaImageView(view: PanoramaView?) {
        if (view != null && !mViews.contains(view)) {
            mViews.addFirst(view)
        }
    }

    // РЕАГИРЕТ НА ИЗМЕНЕНИЯ ЗНАЧЕНИЙ СЕНСОРА И ОБНВОЛЯЕТ VIEW
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
     * TODO: Write the documentation with @link and @param
     */
    fun setMaxRotateRadian(maxRotateRadian: Double) {
        require(!(maxRotateRadian <= 0 || maxRotateRadian > Math.PI / 2)) {
            "There must be the Double value between (0, π/2]."
        }
        mMaxRotateRadian = maxRotateRadian
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int){}

    companion object {
        // For translate nanosecond to second.
        private const val nanoToSeconds = 1.0f / 1000000000.0f
        // Rotation Speed Constants for MaxRotateRadian
        const val SLOW = Math.PI / 2
        const val NORMAL = Math.PI / 9
        const val FAST = Math.PI / 18
    }
}

// --- OLD ON SENSOR CHANGED ---
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
