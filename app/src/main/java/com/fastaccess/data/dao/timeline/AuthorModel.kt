package com.fastaccess.data.dao.timeline

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class AuthorModel : Parcelable {
    var name: String? = null
    var email: String? = null
    var date: Date? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(email)
        dest.writeLong(if (date != null) date!!.time else -1)
    }

    private constructor(`in`: Parcel) {
        name = `in`.readString()
        email = `in`.readString()
        val tmpDate = `in`.readLong()
        date = if (tmpDate == -1L) null else Date(tmpDate)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AuthorModel> = object : Parcelable.Creator<AuthorModel> {
            override fun createFromParcel(source: Parcel): AuthorModel? {
                return AuthorModel(source)
            }

            override fun newArray(size: Int): Array<AuthorModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}