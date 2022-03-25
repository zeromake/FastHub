package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 03 Dec 2016, 11:12 AM
 */
open class RepoPermissionsModel : Parcelable {
    var admin = false
    var push = false
    var pull = false

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByte(if (admin) 1.toByte() else 0.toByte())
        dest.writeByte(if (push) 1.toByte() else 0.toByte())
        dest.writeByte(if (pull) 1.toByte() else 0.toByte())
    }

    protected constructor(`in`: Parcel) {
        admin = `in`.readByte().toInt() != 0
        push = `in`.readByte().toInt() != 0
        pull = `in`.readByte().toInt() != 0
    }

    companion object CREATOR : Parcelable.Creator<RepoPermissionsModel> {
        override fun createFromParcel(parcel: Parcel): RepoPermissionsModel {
            return RepoPermissionsModel(parcel)
        }

        override fun newArray(size: Int): Array<RepoPermissionsModel?> {
            return arrayOfNulls(size)
        }
    }
}