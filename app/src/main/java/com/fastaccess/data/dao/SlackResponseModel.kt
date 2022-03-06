package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel

/**
 * Created by Kosh on 01 May 2017, 1:05 AM
 */
class SlackResponseModel : Parcelable {
    var isOk = false
    var error: String? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeByte(if (isOk) 1.toByte() else 0.toByte())
        dest.writeString(error)
    }

    constructor() {}
    private constructor(`in`: Parcel) {
        isOk = `in`.readByte().toInt() != 0
        error = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SlackResponseModel> =
            object : Parcelable.Creator<SlackResponseModel> {
                override fun createFromParcel(source: Parcel): SlackResponseModel {
                    return SlackResponseModel(source)
                }

                override fun newArray(size: Int): Array<SlackResponseModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}