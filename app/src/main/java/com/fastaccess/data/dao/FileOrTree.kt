package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

class FileOrTree() : Parcelable {
    var type: String? = null

    constructor(parcel: Parcel) : this() {
        type = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FileOrTree> {
        override fun createFromParcel(parcel: Parcel): FileOrTree {
            return FileOrTree(parcel)
        }

        override fun newArray(size: Int): Array<FileOrTree?> {
            return arrayOfNulls(size)
        }
    }
}