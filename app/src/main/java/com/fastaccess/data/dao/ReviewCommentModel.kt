package com.fastaccess.data.dao

import android.os.Parcelable
import android.os.Parcel
import com.fastaccess.data.dao.model.User
import java.util.*

/**
 * Created by Kosh on 04 May 2017, 7:10 PM
 */
class ReviewCommentModel : Parcelable {
    var id: Long = 0
    var url: String? = null
    var pullRequestReviewId: Long = 0
    var diffHunk: String? = null
    var path: String? = null
    var position = 0
    var originalPosition = 0
    var commitId: String? = null
    var originalCommitId: String? = null
    var user: User? = null
    var bodyHtml: String? = null
    var body: String? = null
    var createdAt: Date? = null
    var updatedAt: Date? = null
    var htmlUrl: String? = null
    var pullRequestUrl: String? = null
    var reactions: ReactionsModel? = null
    var authorAssociation: String? = null

    constructor() {}

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ReviewCommentModel
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
        dest.writeString(url)
        dest.writeLong(pullRequestReviewId)
        dest.writeString(diffHunk)
        dest.writeString(path)
        dest.writeInt(position)
        dest.writeInt(originalPosition)
        dest.writeString(commitId)
        dest.writeString(originalCommitId)
        dest.writeParcelable(user, flags)
        dest.writeString(bodyHtml)
        dest.writeString(body)
        dest.writeLong(if (createdAt != null) createdAt!!.time else -1)
        dest.writeLong(if (updatedAt != null) updatedAt!!.time else -1)
        dest.writeString(htmlUrl)
        dest.writeString(pullRequestUrl)
        dest.writeParcelable(reactions, flags)
        dest.writeString(authorAssociation)
    }

    protected constructor(`in`: Parcel) {
        id = `in`.readLong()
        url = `in`.readString()
        pullRequestReviewId = `in`.readLong()
        diffHunk = `in`.readString()
        path = `in`.readString()
        position = `in`.readInt()
        originalPosition = `in`.readInt()
        commitId = `in`.readString()
        originalCommitId = `in`.readString()
        user = `in`.readParcelable(User::class.java.classLoader)
        bodyHtml = `in`.readString()
        body = `in`.readString()
        val tmpCreatedAt = `in`.readLong()
        createdAt = if (tmpCreatedAt == -1L) null else Date(tmpCreatedAt)
        val tmpUpdatedAt = `in`.readLong()
        updatedAt = if (tmpUpdatedAt == -1L) null else Date(tmpUpdatedAt)
        htmlUrl = `in`.readString()
        pullRequestUrl = `in`.readString()
        reactions = `in`.readParcelable(ReactionsModel::class.java.classLoader)
        authorAssociation = `in`.readString()
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<ReviewCommentModel> =
            object : Parcelable.Creator<ReviewCommentModel> {
                override fun createFromParcel(source: Parcel): ReviewCommentModel {
                    return ReviewCommentModel(source)
                }

                override fun newArray(size: Int): Array<ReviewCommentModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}