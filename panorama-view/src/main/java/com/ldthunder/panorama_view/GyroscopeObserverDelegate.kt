package com.ldthunder.panorama_view

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * If you don't want to manually [register][GyroscopeObserver.register]
 * and [unregister][GyroscopeObserver.unregister] the observer, you can use Lifecycle-aware
 * delegate. Example usage:
 * ```
 * class MainActivity : AppCompatActivity() {
 *     private val gyroscopeObserver by GyroscopeObserverDelegate(this, GyroscopeObserver())
 *
 *     override fun onCreate(savedInstanceState: Bundle?){ ... }
 * }
 * ```
 * @param context The context of your activity
 * @param observer The instance of [GyroscopeObserver] class
 * @see GyroscopeObserver

 */
class GyroscopeObserverDelegate(
    private val context: Context,
    private val observer: GyroscopeObserver,
) : ReadOnlyProperty<LifecycleOwner, GyroscopeObserver>, LifecycleEventObserver {

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): GyroscopeObserver {
        thisRef.lifecycle.addObserver(this)
        return observer
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> observer.register(context)
            Lifecycle.Event.ON_PAUSE -> observer.unregister()
            else -> Unit
        }
    }
}