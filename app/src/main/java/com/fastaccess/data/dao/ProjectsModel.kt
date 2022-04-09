package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.dao.ProjectsModel
import com.fastaccess.data.entity.User
import java.util.*

/**
 * Created by kosh on 09/09/2017.
 */
class ProjectsModel : Parcelable {
    var ownerUrl: String? = null
    var url: String? = null
    var htmlUrl: String? = null
    var columnsUrl: String? = null
    var id: Long = 0
    var name: String? = null
    var body: String? = null
    var number = 0
    var state: String? = null
    var creator: User? = null
    var createdAt: Date? = null
    var updatedAt: Date? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(ownerUrl)
        dest.writeString(url)
        dest.writeString(htmlUrl)
        dest.writeString(columnsUrl)
        dest.writeLong(id)
        dest.writeString(name)
        dest.writeString(body)
        dest.writeInt(number)
        dest.writeString(state)
        dest.writeParcelable(creator, flags)
        dest.writeLong(if (createdAt != null) createdAt!!.time else -1)
        dest.writeLong(if (updatedAt != null) updatedAt!!.time else -1)
    }

    constructor() {}
    protected constructor(`in`: Parcel) {
        ownerUrl = `in`.readString()
        url = `in`.readString()
        htmlUrl = `in`.readString()
        columnsUrl = `in`.readString()
        id = `in`.readLong()
        name = `in`.readString()
        body = `in`.readString()
        number = `in`.readInt()
        state = `in`.readString()
        creator = `in`.readParcelable(User::class.java.classLoader)
        val tmpCreatedAt = `in`.readLong()
        createdAt = if (tmpCreatedAt == -1L) null else Date(tmpCreatedAt)
        val tmpUpdatedAt = `in`.readLong()
        updatedAt = if (tmpUpdatedAt == -1L) null else Date(tmpUpdatedAt)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ProjectsModel> =
            object : Parcelable.Creator<ProjectsModel> {
                override fun createFromParcel(source: Parcel): ProjectsModel {
                    return ProjectsModel(source)
                }

                override fun newArray(size: Int): Array<ProjectsModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}