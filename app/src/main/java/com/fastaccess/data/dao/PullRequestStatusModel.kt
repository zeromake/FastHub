package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.types.StatusStateType
import java.util.*

/**
 * Created by Kosh on 10 Apr 2017, 3:15 AM
 */
class PullRequestStatusModel : Parcelable {
    var state: StatusStateType? = null
    var sha: String? = null
    var totalCount = 0
    var statuses: List<StatusesModel>? = null
    var commitUrl: String? = null
    var url: String? = null
    var isMergable = false
    var createdAt: Date? = null
    var mergeableState: String? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if (state == null) -1 else state!!.ordinal)
        dest.writeString(sha)
        dest.writeInt(totalCount)
        dest.writeTypedList(statuses)
        dest.writeString(commitUrl)
        dest.writeString(url)
        dest.writeByte(if (isMergable) 1.toByte() else 0.toByte())
        dest.writeLong(if (createdAt != null) createdAt!!.time else -1)
        dest.writeString(mergeableState)
    }

    protected constructor(`in`: Parcel) {
        val tmpState = `in`.readInt()
        state = if (tmpState == -1) null else StatusStateType.values()[tmpState]
        sha = `in`.readString()
        totalCount = `in`.readInt()
        statuses = `in`.createTypedArrayList(StatusesModel.CREATOR)
        commitUrl = `in`.readString()
        url = `in`.readString()
        isMergable = `in`.readByte().toInt() != 0
        val tmpCreatedAt = `in`.readLong()
        createdAt = if (tmpCreatedAt == -1L) null else Date(tmpCreatedAt)
        mergeableState = `in`.readString()
    }

    companion object CREATOR : Parcelable.Creator<PullRequestStatusModel> {
        override fun createFromParcel(parcel: Parcel): PullRequestStatusModel {
            return PullRequestStatusModel(parcel)
        }

        override fun newArray(size: Int): Array<PullRequestStatusModel?> {
            return arrayOfNulls(size)
        }
    }
}