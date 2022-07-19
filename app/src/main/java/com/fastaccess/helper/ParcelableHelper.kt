package com.fastaccess.helper

import android.os.Parcel
import android.os.Parcelable
import java.util.*

interface KotlinParcelable : Parcelable {
    override fun describeContents() = 0
    override fun writeToParcel(dest: Parcel, flags: Int)
}

inline fun <reified T> parcelableCreator(crossinline create: (Parcel) -> T) =
    object : Parcelable.Creator<T> {
        override fun createFromParcel(source: Parcel) = create(source)
        override fun newArray(size: Int) = arrayOfNulls<T>(size)
    }

fun Parcel.readBooleanCompat() = readInt() != 0
fun Parcel.writeBooleanCompat(value: Boolean) = writeInt(if (value) 1 else 0)


fun Parcel.readAtCompat(): Date? {
    val at = readLong()
    return if (at == -1L) null else Date(at)
}

fun Parcel.writeAtCompat(value: Date?) = writeLong(value?.time ?: -1L)

fun Parcel.writeEnum(value: Enum<*>?) {
    writeInt(value?.ordinal ?: -1)
}

fun <T> Parcel.readEnum(arr: Array<T>): T? {
    val state = readInt()
    return if (state == -1) null else arr[state]
}

inline fun <reified T : List<Any>> Parcel.readList(create: () -> T): T {
    val item = create()
    readList(item, T::class.java.classLoader)
    return item
}