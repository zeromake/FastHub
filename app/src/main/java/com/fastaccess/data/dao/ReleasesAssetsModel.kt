package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.dao.ReleasesAssetsModel
import com.fastaccess.data.entity.User
import java.util.*

/**
 * Created by Kosh on 31 Dec 2016, 1:28 PM
 */
class ReleasesAssetsModel : Parcelable {
    var url: String? = null
    var browserDownloadUrl: String? = null
    var id: Long = 0
    var name: String? = null
    var label: String? = null
    var state: String? = null
    var contentType: String? = null
    var size = 0
    var downloadCount = 0
    var createdAt: Date? = null
    var updatedAt: Date? = null
    var uploader: User? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
        dest.writeString(browserDownloadUrl)
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeString(label)
        dest.writeString(state)
        dest.writeString(contentType)
        dest.writeInt(size)
        dest.writeInt(downloadCount)
        dest.writeLong(if (createdAt != null) createdAt!!.time else -1)
        dest.writeLong(if (updatedAt != null) updatedAt!!.time else -1)
        dest.writeParcelable(uploader, flags)
    }

    constructor() {}
    protected constructor(`in`: Parcel) {
        url = `in`.readString()
        browserDownloadUrl = `in`.readString()
        id = `in`.readLong()
        name = `in`.readString()
        label = `in`.readString()
        state = `in`.readString()
        contentType = `in`.readString()
        size = `in`.readInt()
        downloadCount = `in`.readInt()
        val tmpCreatedAt = `in`.readLong()
        createdAt = if (tmpCreatedAt == -1L) null else Date(tmpCreatedAt)
        val tmpUpdatedAt = `in`.readLong()
        updatedAt = if (tmpUpdatedAt == -1L) null else Date(tmpUpdatedAt)
        uploader = `in`.readParcelable(User::class.java.classLoader)
    }

    companion object CREATOR : Parcelable.Creator<ReleasesAssetsModel> {
        override fun createFromParcel(parcel: Parcel): ReleasesAssetsModel {
            return ReleasesAssetsModel(parcel)
        }

        override fun newArray(size: Int): Array<ReleasesAssetsModel?> {
            return arrayOfNulls(size)
        }
    }
}