package com.fastaccess.data.dao

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.types.StatusStateType
import java.util.*

/**
 * Created by Kosh on 10 Apr 2017, 3:16 AM
 */
class StatusesModel : Parcelable {
    var url: String? = null
    var state: StatusStateType? = null
    var description: String? = null
    var targetUrl: String? = null
    var context: String? = null
    var createdAt: Date? = null
    var updatedAt: Date? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(url)
        dest.writeInt(if (state == null) -1 else state!!.ordinal)
        dest.writeString(description)
        dest.writeString(targetUrl)
        dest.writeString(context)
        dest.writeLong(if (createdAt != null) createdAt!!.time else -1)
        dest.writeLong(if (updatedAt != null) updatedAt!!.time else -1)
    }

    private constructor(`in`: Parcel) {
        url = `in`.readString()
        val tmpState = `in`.readInt()
        state = if (tmpState == -1) null else StatusStateType.values()[tmpState]
        description = `in`.readString()
        targetUrl = `in`.readString()
        context = `in`.readString()
        val tmpCreatedAt = `in`.readLong()
        createdAt = if (tmpCreatedAt == -1L) null else Date(tmpCreatedAt)
        val tmpUpdatedAt = `in`.readLong()
        updatedAt = if (tmpUpdatedAt == -1L) null else Date(tmpUpdatedAt)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<StatusesModel> =
            object : Parcelable.Creator<StatusesModel> {
                override fun createFromParcel(source: Parcel): StatusesModel {
                    return StatusesModel(source)
                }

                override fun newArray(size: Int): Array<StatusesModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}