package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 16 Dec 2016, 11:42 PM
 */
open class MergeRequestModel : Parcelable {
    var commitMessage: String? = null
    var sha: String? = null
    var base: String? = null
    var head: String? = null
    var mergeMethod = "merge"

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(commitMessage)
        dest.writeString(sha)
        dest.writeString(base)
        dest.writeString(head)
    }

    protected constructor(`in`: Parcel) {
        commitMessage = `in`.readString()
        sha = `in`.readString()
        base = `in`.readString()
        head = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MergeRequestModel> =
            object : Parcelable.Creator<MergeRequestModel> {
                override fun createFromParcel(source: Parcel): MergeRequestModel {
                    return MergeRequestModel(source)
                }

                override fun newArray(size: Int): Array<MergeRequestModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}