package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel

/**
 * Created by Kosh on 03 Apr 2017, 7:40 PM
 */
class TeamsModel : Parcelable {
    var id: Long = 0
    var url: String? = null
    var name: String? = null
    var slug: String? = null
    var description: String? = null
    var privacy: String? = null
    var permission: String? = null
    var membersUrl: String? = null
    var repositoriesUrl: String? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(url)
        dest.writeString(name)
        dest.writeString(slug)
        dest.writeString(description)
        dest.writeString(privacy)
        dest.writeString(permission)
        dest.writeString(membersUrl)
        dest.writeString(repositoriesUrl)
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readLong()
        url = `in`.readString()
        name = `in`.readString()
        slug = `in`.readString()
        description = `in`.readString()
        privacy = `in`.readString()
        permission = `in`.readString()
        membersUrl = `in`.readString()
        repositoriesUrl = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TeamsModel> = object : Parcelable.Creator<TeamsModel> {
            override fun createFromParcel(source: Parcel): TeamsModel {
                return TeamsModel(source)
            }

            override fun newArray(size: Int): Array<TeamsModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}