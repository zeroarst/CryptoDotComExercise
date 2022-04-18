package com.crypto.exercise.activities.delegations

import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> Bundle.put(key: String, value: T) {
    when (value) {
        is Unit -> {}
        is Boolean -> putBoolean(key, value)
        is String -> putString(key, value)
        is Int -> putInt(key, value)
        is IntArray -> putIntArray(key, value)
        is Short -> putShort(key, value)
        is Long -> putLong(key, value)
        is LongArray -> putLongArray(key, value)
        is Byte -> putByte(key, value)
        is ByteArray -> putByteArray(key, value)
        is Char -> putChar(key, value)
        is CharArray -> putCharArray(key, value)
        is CharSequence -> putCharSequence(key, value)
        is Float -> putFloat(key, value)
        is Bundle -> putBundle(key, value)
        is Parcelable -> putParcelable(key, value)
        is Serializable -> putSerializable(key, value)
        else -> throw IllegalStateException("Type of property $key is not supported. value=$value")
    }
}

class FragmentArgumentDelegate<T>(private val defaultIfNull: T? = null) : ReadWriteProperty<Fragment, T> {
    
    @Suppress("UNCHECKED_CAST")
    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>
    ): T {
        val key = property.name
        return thisRef.arguments?.get(key) as? T ?: defaultIfNull
        ?: throw IllegalStateException("Property \"${property.name}\" could not be null and no default value is given")
    }
    
    override fun setValue(
        thisRef: Fragment,
        property: KProperty<*>, value: T
    ) {
        val args = thisRef.arguments
            ?: Bundle().also(thisRef::setArguments)
        val key = property.name
        args.put(key, value)
    }
}

class FragmentNullableArgumentDelegate<T> :
    ReadWriteProperty<Fragment, T> {
    
    @Suppress("UNCHECKED_CAST")
    override fun getValue(
        thisRef: Fragment,
        property: KProperty<*>
    ): T {
        val key = property.name
        return thisRef.arguments?.get(key) as T
    }
    
    override fun setValue(
        thisRef: Fragment,
        property: KProperty<*>, value: T
    ) {
        val args = thisRef.arguments
            ?: Bundle().also(thisRef::setArguments)
        val key = property.name
        value?.let { args.put(key, it) } ?: args.remove(key)
    }
}

/**
 * @param defaultIfNull note if not given it needs to be guaranteed the initial value is set.
 */
fun <T> argument(defaultIfNull: T? = null): ReadWriteProperty<Fragment, T> =
    FragmentArgumentDelegate(defaultIfNull)

fun <T> argumentNullable(): ReadWriteProperty<Fragment, T?> =
    FragmentNullableArgumentDelegate()