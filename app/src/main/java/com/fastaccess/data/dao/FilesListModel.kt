package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import java.io.Serializable

/**
 * Created by Kosh on 12 Nov 2016, 11:09 AM
 */
open class FilesListModel : Parcelable, Serializable {
    var filename: String? = null
    var type: String? = null
    var rawUrl: String? = null
    var size: Long? = null
    var content: String? = null
    var needFetching: Boolean? = null
    var language: String? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(filename)
        dest.writeString(type)
        dest.writeString(rawUrl)
        dest.writeValue(size)
        dest.writeString(content)
        dest.writeValue(needFetching)
        dest.writeString(language)
    }

    protected constructor(`in`: Parcel) {
        filename = `in`.readString()
        type = `in`.readString()
        rawUrl = `in`.readString()
        size = `in`.readValue(Long::class.java.classLoader) as Long?
        content = `in`.readString()
        needFetching = `in`.readValue(Boolean::class.java.classLoader) as Boolean?
        language = `in`.readString()
    }


    companion object CREATOR : Parcelable.Creator<FilesListModel> {
        override fun createFromParcel(parcel: Parcel): FilesListModel {
            return FilesListModel(parcel)
        }

        override fun newArray(size: Int): Array<FilesListModel?> {
            return arrayOfNulls(size)
        }
    }
}