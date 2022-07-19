package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.entity.Repo

/**
 * Created by Kosh on 19 Feb 2017, 1:17 AM
 */
open class SearchCodeModel : Parcelable {
    var name: String? = null
    var path: String? = null
    var sha: String? = null
    var url: String? = null
    var gitUrl: String? = null
    var repository: Repo? = null
    var score = 0.0
    var htmlUrl: String? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(path)
        dest.writeString(sha)
        dest.writeString(url)
        dest.writeString(gitUrl)
        dest.writeParcelable(repository, flags)
        dest.writeDouble(score)
    }

    protected constructor(`in`: Parcel) {
        name = `in`.readString()
        path = `in`.readString()
        sha = `in`.readString()
        url = `in`.readString()
        gitUrl = `in`.readString()
        repository = `in`.readParcelable(Repo::class.java.classLoader)
        score = `in`.readDouble()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SearchCodeModel> =
            object : Parcelable.Creator<SearchCodeModel> {
                override fun createFromParcel(source: Parcel): SearchCodeModel {
                    return SearchCodeModel(source)
                }

                override fun newArray(size: Int): Array<SearchCodeModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
