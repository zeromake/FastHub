package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 09 Nov 2016, 11:28 PM
 */
class AccessTokenModel : Parcelable {
    var id: Long = 0
    var token: String? = null
    var hashedToken: String? = null
    var accessToken: String? = null
    var tokenType: String? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(token)
        dest.writeString(hashedToken)
        dest.writeString(accessToken)
        dest.writeString(tokenType)
    }

    private constructor(`in`: Parcel) {
        id = `in`.readLong()
        token = `in`.readString()
        hashedToken = `in`.readString()
        accessToken = `in`.readString()
        tokenType = `in`.readString()
    }

    companion object CREATOR : Parcelable.Creator<AccessTokenModel> {
        override fun createFromParcel(parcel: Parcel): AccessTokenModel {
            return AccessTokenModel(parcel)
        }

        override fun newArray(size: Int): Array<AccessTokenModel?> {
            return arrayOfNulls(size)
        }
    }
}