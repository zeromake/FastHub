package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.entity.User
import java.util.*

/**
 * Created by Kosh on 10 Apr 2017, 4:26 PM
 */
class ReviewModel : Parcelable {
    var id: Long = 0
    var user: User? = null
    var bodyHtml: String? = null
    var state: String? = null
    var submittedAt: Date? = null
    var commitId: String? = null
    var diffText: String? = null
    var comments: List<ReviewCommentModel>? = null
    var reactions: ReactionsModel? = null
    var bodyText: String? = null

    constructor() {}

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ReviewModel
        return id == that.id
    }

    override fun hashCode(): Int {
        return (id xor (id ushr 32)).toInt()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeParcelable(user, flags)
        dest.writeString(bodyHtml)
        dest.writeString(state)
        dest.writeLong(if (submittedAt != null) submittedAt!!.time else -1)
        dest.writeString(commitId)
        dest.writeString(diffText)
        dest.writeTypedList(comments)
        dest.writeParcelable(reactions, flags)
        dest.writeString(bodyText)
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readLong()
        user = `in`.readParcelable(User::class.java.classLoader)
        bodyHtml = `in`.readString()
        state = `in`.readString()
        val tmpSubmittedAt = `in`.readLong()
        submittedAt = if (tmpSubmittedAt == -1L) null else Date(tmpSubmittedAt)
        commitId = `in`.readString()
        diffText = `in`.readString()
        comments = `in`.createTypedArrayList(ReviewCommentModel.CREATOR)
        reactions = `in`.readParcelable(ReactionsModel::class.java.classLoader)
        bodyText = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ReviewModel> = object : Parcelable.Creator<ReviewModel> {
            override fun createFromParcel(source: Parcel): ReviewModel? {
                return ReviewModel(source)
            }

            override fun newArray(size: Int): Array<ReviewModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}