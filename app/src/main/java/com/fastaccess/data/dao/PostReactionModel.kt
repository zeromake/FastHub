package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.utils.ParcelUtil

/**
 * Created by Kosh on 29 Mar 2017, 9:50 PM
 */
class PostReactionModel  : Parcelable {

    private var content: String? = null
    private constructor(`in`: Parcel) {
         content = `in`.readString()
    }
    constructor(c: String) {
        content = c
    }
    private constructor() {}
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(content)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<PostReactionModel> = ParcelUtil.createParcel { PostReactionModel() }
    }

}