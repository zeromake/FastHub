package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.dao.SimpleUrlsModel

/**
 * Created by Kosh on 31 Dec 2016, 3:32 PM
 */
open class SimpleUrlsModel : Parcelable {
    var item: String
    var url: String?

    constructor(item: String, url: String?) {
        this.item = item
        this.url = url
    }

    override fun toString(): String {
        return item
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(item)
        dest.writeString(url)
    }

    protected constructor(`in`: Parcel) {
        item = `in`.readString()!!
        url = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SimpleUrlsModel> =
            object : Parcelable.Creator<SimpleUrlsModel> {
                override fun createFromParcel(source: Parcel): SimpleUrlsModel {
                    return SimpleUrlsModel(source)
                }

                override fun newArray(size: Int): Array<SimpleUrlsModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}