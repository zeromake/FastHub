package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.entity.Commit
import com.google.gson.annotations.SerializedName

/**
 * Created by Kosh on 03 Mar 2017, 9:08 PM
 */
class BranchesModel : Parcelable {
    var name: String? = null
    var commit: Commit? = null

    @SerializedName("protected")
    var protectedBranch = false
    var protectionUrl: String? = null
    var isTag = false

    constructor() {}

    override fun toString(): String {
        return name!!
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeParcelable(commit, flags)
        dest.writeByte(if (protectedBranch) 1.toByte() else 0.toByte())
        dest.writeString(protectionUrl)
        dest.writeByte(if (isTag) 1.toByte() else 0.toByte())
    }

    private constructor(`in`: Parcel) {
        name = `in`.readString()
        commit = `in`.readParcelable(Commit::class.java.classLoader)
        protectedBranch = `in`.readByte().toInt() != 0
        protectionUrl = `in`.readString()
        isTag = `in`.readByte().toInt() != 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BranchesModel> =
            object : Parcelable.Creator<BranchesModel> {
                override fun createFromParcel(source: Parcel): BranchesModel {
                    return BranchesModel(source)
                }

                override fun newArray(size: Int): Array<BranchesModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}
