package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Kosh on 08 Dec 2016, 8:57 PM
 */
open class GithubState : Parcelable {
    var additions = 0
    var deletions = 0
    var total = 0

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(additions)
        dest.writeInt(deletions)
        dest.writeInt(total)
    }

    protected constructor(`in`: Parcel) {
        additions = `in`.readInt()
        deletions = `in`.readInt()
        total = `in`.readInt()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GithubState> = object : Parcelable.Creator<GithubState> {
            override fun createFromParcel(source: Parcel): GithubState {
                return GithubState(source)
            }

            override fun newArray(size: Int): Array<GithubState?> {
                return arrayOfNulls(size)
            }
        }
    }
}