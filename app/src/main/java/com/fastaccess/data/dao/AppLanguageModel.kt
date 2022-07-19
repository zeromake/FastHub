package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by kosh on 20/07/2017.
 */
class AppLanguageModel : Parcelable {
    var value: String?
    var label: String?

    constructor(value: String?, label: String?) {
        this.value = value
        this.label = label
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(value)
        dest.writeString(label)
    }

    private constructor(`in`: Parcel) {
        value = `in`.readString()
        label = `in`.readString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as AppLanguageModel
        return if (label != null) label == that.label else that.label == null
    }

    override fun hashCode(): Int {
        return if (label != null) label.hashCode() else 0
    }

    companion object CREATOR : Parcelable.Creator<AppLanguageModel> {
        override fun createFromParcel(parcel: Parcel): AppLanguageModel {
            return AppLanguageModel(parcel)
        }

        override fun newArray(size: Int): Array<AppLanguageModel?> {
            return arrayOfNulls(size)
        }
    }
}