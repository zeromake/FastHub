package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 15 Nov 2016, 7:04 PM
 */
open class IssuesPageable<M> : Parcelable {
    var first = 0
    var next = 0
    var prev = 0
    var last = 0
    var totalCount = 0
    var isIncompleteResults = false
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
        dest.writeByte(if (isIncompleteResults) 1.toByte() else 0.toByte())
    }

    protected constructor(`in`: Parcel) {
        first = `in`.readInt()
        next = `in`.readInt()
        prev = `in`.readInt()
        last = `in`.readInt()
        totalCount = `in`.readInt()
        isIncompleteResults = `in`.readByte().toInt() != 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<IssuesPageable<*>> =
            object : Parcelable.Creator<IssuesPageable<*>> {
                override fun createFromParcel(source: Parcel): IssuesPageable<*> {
                    return IssuesPageable<Any?>(source)
                }

                override fun newArray(size: Int): Array<IssuesPageable<*>?> {
                    return arrayOfNulls(size)
                }
            }
    }
}