package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel

/**
 * Created by Kosh on 01 Jan 2017, 1:15 PM
 */
open class LicenseModel : Parcelable {
    var key: String? = null
    var name: String? = null
    var spdxId: String? = null
    var url: String? = null
    var isFeatured = false

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(key)
        dest.writeString(name)
        dest.writeString(spdxId)
        dest.writeString(url)
        dest.writeByte(if (isFeatured) 1.toByte() else 0.toByte())
    }

    protected constructor(`in`: Parcel) {
        key = `in`.readString()
        name = `in`.readString()
        spdxId = `in`.readString()
        url = `in`.readString()
        isFeatured = `in`.readByte().toInt() != 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<LicenseModel> = object : Parcelable.Creator<LicenseModel> {
            override fun createFromParcel(source: Parcel): LicenseModel {
                return LicenseModel(source)
            }

            override fun newArray(size: Int): Array<LicenseModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}