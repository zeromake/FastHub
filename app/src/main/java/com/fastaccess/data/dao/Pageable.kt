package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel

/**
 * Created by Kosh on 15 Nov 2016, 7:04 PM
 */
class Pageable<M : Parcelable?> : Parcelable {
    var first = 0
    var next = 0
    var prev = 0
    var last = 0
    var totalCount = 0
    var incompleteResults = false
    var items: List<M>? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(first)
        dest.writeInt(next)
        dest.writeInt(prev)
        dest.writeInt(last)
        dest.writeInt(totalCount)
        dest.writeByte(if (incompleteResults) 1.toByte() else 0.toByte())
        dest.writeTypedList(items)
    }

    protected constructor(`in`: Parcel) {
        first = `in`.readInt()
        next = `in`.readInt()
        prev = `in`.readInt()
        last = `in`.readInt()
        totalCount = `in`.readInt()
        incompleteResults = `in`.readByte().toInt() != 0
        `in`.readList(items!!, items!!.javaClass.classLoader)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Pageable<Parcelable>> =
            object : Parcelable.Creator<Pageable<Parcelable>> {
                override fun createFromParcel(source: Parcel): Pageable<Parcelable> {
                    return Pageable(source)
                }
                override fun newArray(size: Int): Array<Pageable<Parcelable>?> {
                    return arrayOfNulls(size)
                }
            }
    }
}