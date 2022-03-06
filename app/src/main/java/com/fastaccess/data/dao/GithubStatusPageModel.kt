package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

class GithubStatusPageModel(): Parcelable {
    var id: String? = null
    var name: String? = null
    var url: String? = null
    @SerializedName("time_zone") var timeZone: String? = null
    @SerializedName("update_at") var updateAt: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        name = parcel.readString()
        url = parcel.readString()
        timeZone = parcel.readString()
        updateAt = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeString(timeZone)
        parcel.writeString(updateAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GithubStatusPageModel> {
        override fun createFromParcel(parcel: Parcel): GithubStatusPageModel {
            return GithubStatusPageModel(parcel)
        }

        override fun newArray(size: Int): Array<GithubStatusPageModel?> {
            return arrayOfNulls(size)
        }
    }
}