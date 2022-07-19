package com.fastaccess.data.dao.timeline

import android.os.Parcel
import android.os.Parcelable

class ParentsModel : Parcelable {
    private var sha: String? = null
    private var url: String? = null
    private var htmlUrl: String? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(sha)
        dest.writeString(url)
        dest.writeString(htmlUrl)
    }

    constructor() {}
    private constructor(`in`: Parcel) {
        sha = `in`.readString()
        url = `in`.readString()
        htmlUrl = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ParentsModel> = object : Parcelable.Creator<ParentsModel> {
            override fun createFromParcel(source: Parcel): ParentsModel? {
                return ParentsModel(source)
            }

            override fun newArray(size: Int): Array<ParentsModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}