package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 11 Jun 2017, 7:13 PM
 */
class WikiModel : Parcelable {
    var pageName: String? = null
    var title: String? = null
    var summary: String? = null
    var action: String? = null
    var sha: String? = null
    var htmlUrl: String? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(pageName)
        dest.writeString(title)
        dest.writeString(summary)
        dest.writeString(action)
        dest.writeString(sha)
        dest.writeString(htmlUrl)
    }

    private constructor(`in`: Parcel) {
        pageName = `in`.readString()
        title = `in`.readString()
        summary = `in`.readString()
        action = `in`.readString()
        sha = `in`.readString()
        htmlUrl = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<WikiModel> = object : Parcelable.Creator<WikiModel> {
            override fun createFromParcel(source: Parcel): WikiModel {
                return WikiModel(source)
            }

            override fun newArray(size: Int): Array<WikiModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}