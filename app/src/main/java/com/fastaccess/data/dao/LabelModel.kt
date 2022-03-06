package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel

/**
 * Created by Kosh on 08 Dec 2016, 9:05 PM
 */
open class LabelModel : Parcelable {
    var url: String? = null
    var name: String? = null
    var color: String? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
        dest.writeString(name)
        dest.writeString(color)
    }

    protected constructor(`in`: Parcel) {
        url = `in`.readString()
        name = `in`.readString()
        color = `in`.readString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as LabelModel
        return if (name != null) name == that.name else that.name == null
    }

    override fun hashCode(): Int {
        return if (name != null) name.hashCode() else 0
    }

    override fun toString(): String {
        return "LabelModel{" +
                "url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", color='" + color + '\'' +
                '}'
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<LabelModel> = object : Parcelable.Creator<LabelModel> {
            override fun createFromParcel(source: Parcel): LabelModel {
                return LabelModel(source)
            }

            override fun newArray(size: Int): Array<LabelModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}