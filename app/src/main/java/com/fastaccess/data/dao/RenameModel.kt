package com.fastaccess.data.dao

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import android.os.Parcel
import com.fastaccess.data.dao.RenameModel

/**
 * Created by Kosh on 13 Dec 2016, 12:33 AM
 */
class RenameModel : Parcelable {
    @SerializedName("from")
    var fromValue: String? = null

    @SerializedName("to")
    var toValue: String? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(fromValue)
        dest.writeString(toValue)
    }

    protected constructor(`in`: Parcel) {
        fromValue = `in`.readString()
        toValue = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<RenameModel> = object : Parcelable.Creator<RenameModel> {
            override fun createFromParcel(source: Parcel): RenameModel {
                return RenameModel(source)
            }

            override fun newArray(size: Int): Array<RenameModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}