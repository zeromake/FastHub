package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel

/**
 * Created by Kosh on 27 May 2017, 9:47 PM
 */
class LanguageColorModel : Parcelable {
    var color: String? = null
    var url: String? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(color)
        dest.writeString(url)
    }

    override fun toString(): String {
        return "LanguageColorModel(color=$color, url=$url)"
    }

    public constructor() {}

    private constructor(`in`: Parcel) {
        color = `in`.readString()
        url = `in`.readString()
    }

    companion object CREATOR: Parcelable.Creator<LanguageColorModel> {
        override fun createFromParcel(source: Parcel?): LanguageColorModel {
            return LanguageColorModel(source!!)
        }

        override fun newArray(size: Int): Array<LanguageColorModel?> {
            return arrayOfNulls(size)
        }
    }
}