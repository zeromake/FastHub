package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.dao.MergeResponseModel

/**
 * Created by Kosh on 16 Dec 2016, 11:40 PM
 */
open class MergeResponseModel : Parcelable {
    var sha: String? = null
    var isMerged = false
    var message: String? = null
    var documentationUrl: String? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(sha)
        dest.writeByte(if (isMerged) 1.toByte() else 0.toByte())
        dest.writeString(message)
        dest.writeString(documentationUrl)
    }

    protected constructor(`in`: Parcel) {
        sha = `in`.readString()
        isMerged = `in`.readByte().toInt() != 0
        message = `in`.readString()
        documentationUrl = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MergeResponseModel> =
            object : Parcelable.Creator<MergeResponseModel> {
                override fun createFromParcel(source: Parcel): MergeResponseModel {
                    return MergeResponseModel(source)
                }

                override fun newArray(size: Int): Array<MergeResponseModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}