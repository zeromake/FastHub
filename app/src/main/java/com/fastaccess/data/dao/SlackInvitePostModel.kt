package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 01 May 2017, 1:06 AM
 */
class SlackInvitePostModel : Parcelable {
    var email: String? = null
    var firstName: String? = null
    var lastName: String? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(email)
        dest.writeString(firstName)
        dest.writeString(lastName)
    }

    constructor() {}
    private constructor(`in`: Parcel) {
        email = `in`.readString()
        firstName = `in`.readString()
        lastName = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SlackInvitePostModel> =
            object : Parcelable.Creator<SlackInvitePostModel> {
                override fun createFromParcel(source: Parcel): SlackInvitePostModel? {
                    return SlackInvitePostModel(source)
                }

                override fun newArray(size: Int): Array<SlackInvitePostModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}