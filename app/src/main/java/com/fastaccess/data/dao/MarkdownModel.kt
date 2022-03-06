package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.dao.MarkdownModel

/**
 * Created by Kosh on 18 Feb 2017, 7:20 PM
 */
open class MarkdownModel : Parcelable {
    var text: String? = null
    var mode: String? = "gfm"
    var context: String? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(text)
        dest.writeString(mode)
        dest.writeString(context)
    }

    protected constructor(`in`: Parcel) {
        text = `in`.readString()
        mode = `in`.readString()
        context = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MarkdownModel> =
            object : Parcelable.Creator<MarkdownModel> {
                override fun createFromParcel(source: Parcel): MarkdownModel {
                    return MarkdownModel(source)
                }

                override fun newArray(size: Int): Array<MarkdownModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}