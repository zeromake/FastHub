package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.dao.ProjectCardModel
import com.fastaccess.data.dao.model.User
import java.util.*

/**
 * Created by Hashemsergani on 11.09.17.
 */
open class ProjectCardModel : Parcelable {
    var url: String? = null
    var columnUrl: String? = null
    var contentUrl: String? = null
    var id: Int? = null
    var note: String? = null
    var creator: User? = null
    var createdAt: Date? = null
    var updatedAt: Date? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
        dest.writeString(columnUrl)
        dest.writeString(contentUrl)
        dest.writeValue(id)
        dest.writeString(note)
        dest.writeParcelable(creator, flags)
        dest.writeLong(if (createdAt != null) createdAt!!.time else -1)
        dest.writeLong(if (updatedAt != null) updatedAt!!.time else -1)
    }

    protected constructor(`in`: Parcel) {
        url = `in`.readString()
        columnUrl = `in`.readString()
        contentUrl = `in`.readString()
        id = `in`.readValue(Int::class.java.classLoader) as Int?
        note = `in`.readString()
        creator = `in`.readParcelable(User::class.java.classLoader)
        val tmpCreatedAt = `in`.readLong()
        createdAt = if (tmpCreatedAt == -1L) null else Date(tmpCreatedAt)
        val tmpUpdatedAt = `in`.readLong()
        updatedAt = if (tmpUpdatedAt == -1L) null else Date(tmpUpdatedAt)
    }


    companion object CREATOR : Parcelable.Creator<ProjectCardModel> {
        override fun createFromParcel(parcel: Parcel): ProjectCardModel {
            return ProjectCardModel(parcel)
        }

        override fun newArray(size: Int): Array<ProjectCardModel?> {
            return arrayOfNulls(size)
        }
    }
}