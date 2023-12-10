package com.ldthunder.panorama_view

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

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
            Lifecycle.Event.ON_RESUME -> {
                observer.register(context)
            }

            Lifecycle.Event.ON_PAUSE -> {
                observer.unregister()
            }

            else -> Unit
        }
    }
}