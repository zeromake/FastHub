package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.dao.ProUsersModel

/**
 * Created by Hashemsergani on 03.10.17.
 */
open class ProUsersModel : Parcelable {
    var count = 0
    var isAllowed = false
    var type = 0
    var isBlocked = false
    override fun toString(): String {
        return "ProUsersModel{" +
                ", count=" + count +
                ", allowed=" + isAllowed +
                ", type=" + type +
                '}'
    }

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(count)
        dest.writeByte(if (isAllowed) 1.toByte() else 0.toByte())
        dest.writeInt(type)
        dest.writeByte(if (isBlocked) 1.toByte() else 0.toByte())
    }

    protected constructor(`in`: Parcel) {
        count = `in`.readInt()
        isAllowed = `in`.readByte().toInt() != 0
        type = `in`.readInt()
        isBlocked = `in`.readByte().toInt() != 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ProUsersModel> =
            object : Parcelable.Creator<ProUsersModel> {
                override fun createFromParcel(source: Parcel): ProUsersModel {
                    return ProUsersModel(source)
                }

                override fun newArray(size: Int): Array<ProUsersModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}