package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

class GithubStatusComponentModel() : Parcelable {
    var id: String? = null
    var name: String? = null
    var status: String? = null
    constructor(parcel: Parcel) : this() {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(status)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        id = parcel.readString()
        name = parcel.readString()
        status = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GithubStatusComponentModel> {
        override fun createFromParcel(parcel: Parcel): GithubStatusComponentModel {
            return GithubStatusComponentModel(parcel)
        }

        override fun newArray(size: Int): Array<GithubStatusComponentModel?> {
            return arrayOfNulls(size)
        }
    }
}