package com.fastaccess.ui.delegate

import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

inline fun <reified T : View> viewFind(@IdRes id: Int) =
    FragmentViewFindDelegate<T>(id)

internal object UNINITIALIZED

class FragmentViewFindDelegate<T : View>(
    @IdRes private val id: Int,
) : ReadOnlyProperty<Fragment, T> {
    private var value: Any? = UNINITIALIZED
    private val lock = this

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (value !== UNINITIALIZED) {
            @Suppress("UNCHECKED_CAST")
            return value as T
        }
        return synchronized(lock) {
            val v2 = value
            if (v2 !== UNINITIALIZED) {
                @Suppress("UNCHECKED_CAST") (v2 as T)
            } else {
                value = thisRef.requireView().findViewById(id)
                @Suppress("UNCHECKED_CAST")
                value as T
            }
        }
    }
}