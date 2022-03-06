package com.fastaccess.data.dao

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import android.os.Parcel

/**
 * Created by Kosh on 22 May 2017, 8:36 PM
 */
class EditReviewCommentModel : Parcelable {
    var groupPosition = 0
    var commentPosition = 0
    var comment: String? = null
    var commentModel: ReviewCommentModel? = null

    @SerializedName("in_reply_to")
    var inReplyTo: Long = 0

    constructor() {}

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(groupPosition)
        dest.writeInt(commentPosition)
        dest.writeString(comment)
        dest.writeParcelable(commentModel, flags)
        dest.writeLong(inReplyTo)
    }

    private constructor(`in`: Parcel) {
        groupPosition = `in`.readInt()
        commentPosition = `in`.readInt()
        comment = `in`.readString()
        commentModel = `in`.readParcelable(ReviewCommentModel::class.java.classLoader)
        inReplyTo = `in`.readLong()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<EditReviewCommentModel> =
            object : Parcelable.Creator<EditReviewCommentModel> {
                override fun createFromParcel(source: Parcel): EditReviewCommentModel {
                    return EditReviewCommentModel(source)
                }

                override fun newArray(size: Int): Array<EditReviewCommentModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}