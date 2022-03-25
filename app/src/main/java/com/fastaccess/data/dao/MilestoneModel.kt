package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.model.User
import java.util.*

/**
 * Created by Kosh on 08 Dec 2016, 8:47 PM
 */
class MilestoneModel : Parcelable {
    var id: Long = 0
    var url: String? = null
    var title: String? = null
    var state: String? = null
    var description: String? = null
    var number = 0
    var creator: User? = null
    var htmlUr: String? = null
    var openIssues = 0
    var closedIssues = 0
    var createdAt: Date? = null
    var updatedAt: Date? = null
    var closedAt: Date? = null
    var dueOn: Date? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(url)
        dest.writeString(title)
        dest.writeString(state)
        dest.writeString(description)
        dest.writeInt(number)
        dest.writeParcelable(creator, flags)
        dest.writeString(htmlUr)
        dest.writeInt(openIssues)
        dest.writeInt(closedIssues)
        dest.writeLong(if (createdAt != null) createdAt!!.time else -1)
        dest.writeLong(if (updatedAt != null) updatedAt!!.time else -1)
        dest.writeLong(if (closedAt != null) closedAt!!.time else -1)
        dest.writeLong(if (dueOn != null) dueOn!!.time else -1)
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readLong()
        url = `in`.readString()
        title = `in`.readString()
        state = `in`.readString()
        description = `in`.readString()
        number = `in`.readInt()
        creator = `in`.readParcelable(User::class.java.classLoader)
        htmlUr = `in`.readString()
        openIssues = `in`.readInt()
        closedIssues = `in`.readInt()
        val tmpCreatedAt = `in`.readLong()
        createdAt = if (tmpCreatedAt == -1L) null else Date(tmpCreatedAt)
        val tmpUpdatedAt = `in`.readLong()
        updatedAt = if (tmpUpdatedAt == -1L) null else Date(tmpUpdatedAt)
        val tmpClosedAt = `in`.readLong()
        closedAt = if (tmpClosedAt == -1L) null else Date(tmpClosedAt)
        val tmpDueOn = `in`.readLong()
        dueOn = if (tmpDueOn == -1L) null else Date(tmpDueOn)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<MilestoneModel> =
            object : Parcelable.Creator<MilestoneModel> {
                override fun createFromParcel(source: Parcel): MilestoneModel? {
                    return MilestoneModel(source)
                }

                override fun newArray(size: Int): Array<MilestoneModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}