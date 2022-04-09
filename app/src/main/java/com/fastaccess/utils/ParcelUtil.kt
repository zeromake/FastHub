package com.fastaccess.utils

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class ParcelUtil {
    companion object {
        inline fun <reified T : Parcelable> createParcel(crossinline createFromParcel: (Parcel) -> T?): Parcelable.Creator<T> =
            object : Parcelable.Creator<T> {
                override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
                override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
            }
    }
}