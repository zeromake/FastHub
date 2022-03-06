package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import java.util.*

/**
 * Created by Hashemsergani on 18.10.17.
 */
open class GitHubStatusModel : Parcelable {
    var status: String? = null
    var body: String? = null
    var createdOn: Date? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(status)
        dest.writeString(body)
        dest.writeLong(if (createdOn != null) createdOn!!.time else -1)
    }

    constructor() {}
    protected constructor(`in`: Parcel) {
        status = `in`.readString()
        body = `in`.readString()
        val tmpCreatedOn = `in`.readLong()
        createdOn = if (tmpCreatedOn == -1L) null else Date(tmpCreatedOn)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GitHubStatusModel> =
            object : Parcelable.Creator<GitHubStatusModel> {
                override fun createFromParcel(source: Parcel): GitHubStatusModel {
                    return GitHubStatusModel(source)
                }

                override fun newArray(size: Int): Array<GitHubStatusModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}