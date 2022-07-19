package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by Hashemsergani on 11.09.17.
 */
open class ProjectColumnModel : Parcelable {
    var id: Long? = null
    var name: String? = null
    var url: String? = null
    var projectUrl: String? = null
    var cardsUrl: String? = null
    var createdAt: Date? = null
    var updatedAt: Date? = null
    fun getId(): Long {
        return id!!
    }

    fun setId(id: Long) {
        this.id = id
    }

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeValue(id)
        dest.writeString(name)
        dest.writeString(url)
        dest.writeString(projectUrl)
        dest.writeString(cardsUrl)
        dest.writeLong(if (createdAt != null) createdAt!!.time else -1)
        dest.writeLong(if (updatedAt != null) updatedAt!!.time else -1)
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readValue(Long::class.java.classLoader) as Long?
        name = `in`.readString()
        url = `in`.readString()
        projectUrl = `in`.readString()
        cardsUrl = `in`.readString()
        val tmpCreatedAt = `in`.readLong()
        createdAt = if (tmpCreatedAt == -1L) null else Date(tmpCreatedAt)
        val tmpUpdatedAt = `in`.readLong()
        updatedAt = if (tmpUpdatedAt == -1L) null else Date(tmpUpdatedAt)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ProjectColumnModel
        return id == that.id
    }

    override fun hashCode(): Int {
        return if (id != null) id.hashCode() else 0
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ProjectColumnModel> =
            object : Parcelable.Creator<ProjectColumnModel> {
                override fun createFromParcel(source: Parcel): ProjectColumnModel {
                    return ProjectColumnModel(source)
                }

                override fun newArray(size: Int): Array<ProjectColumnModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}