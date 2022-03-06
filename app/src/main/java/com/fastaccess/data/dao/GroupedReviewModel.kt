package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import java.util.*

/**
 * Created by Kosh on 07 May 2017, 5:08 PM
 */
class GroupedReviewModel : Parcelable {
    var position //to group with!
            = 0
    var diffText: String? = null
    var date: Date? = null
    var path: String? = null
    var id: Long = 0
    var comments: ArrayList<ReviewCommentModel>? = null

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(position)
        dest.writeString(diffText)
        dest.writeLong(if (date != null) date!!.time else -1)
        dest.writeString(path)
        dest.writeLong(id)
        dest.writeTypedList(comments)
    }

    private constructor(`in`: Parcel) {
        position = `in`.readInt()
        diffText = `in`.readString()
        val tmpDate = `in`.readLong()
        date = if (tmpDate == -1L) null else Date(tmpDate)
        path = `in`.readString()
        id = `in`.readLong()
        comments = `in`.createTypedArrayList(ReviewCommentModel.CREATOR)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GroupedReviewModel> =
            object : Parcelable.Creator<GroupedReviewModel> {
                override fun createFromParcel(source: Parcel): GroupedReviewModel {
                    return GroupedReviewModel(source)
                }

                override fun newArray(size: Int): Array<GroupedReviewModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}