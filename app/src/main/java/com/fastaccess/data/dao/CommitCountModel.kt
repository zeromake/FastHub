package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.dao.CommitCountModel
import java.util.ArrayList

/**
 * Created by Kosh on 01 Apr 2017, 12:42 PM
 */
class CommitCountModel : Parcelable {
    var all: List<Int?>? = null
    var owner: List<Int?>? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeList(all)
        dest.writeList(owner)
    }

    constructor() {}
    private constructor(`in`: Parcel) {
        all = ArrayList()
        `in`.readList(all as ArrayList<Int?>, Int::class.java.classLoader)
        owner = ArrayList()
        `in`.readList(owner as ArrayList<Int?>, Int::class.java.classLoader)
    }

    companion object CREATOR : Parcelable.Creator<CommitCountModel> {
        override fun createFromParcel(parcel: Parcel): CommitCountModel {
            return CommitCountModel(parcel)
        }

        override fun newArray(size: Int): Array<CommitCountModel?> {
            return arrayOfNulls(size)
        }
    }
}