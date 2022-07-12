package com.fastaccess.data.dao.timeline

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.ReactionsModel
import com.fastaccess.data.entity.Comment
import com.fastaccess.data.entity.User
import java.util.*

/**
 * Created by Kosh on 16 Mar 2017, 7:24 PM
 */
class CommentEvent : Parcelable {
    var id: Long = 0
    var user: User? = null
    var url: String? = null
    var body: String? = null
    var bodyHtml: String? = null
    var htmlUrl: String? = null
    var createdAt: Date? = null
    var updatedAt: Date? = null
    var position = 0
    var line = 0
    var path: String? = null
    var commitId: String? = null
    var repoId: String? = null
    var login: String? = null
    var gistId: String? = null
    var issueId: String? = null
    var pullRequestId: String? = null
    var reactions: ReactionsModel? = null
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Comment
        return id == that.id
    }

    override fun hashCode(): Int {
        return (id xor (id ushr 32)).toInt()
    }

    constructor() {}

    override fun toString(): String {
        return "CommentEvent{" +
                "id=" + id +
                ", user=" + user +
                ", url='" + url + '\'' +
                ", body='" + body + '\'' +
                ", bodyHtml='" + bodyHtml + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", position=" + position +
                ", line=" + line +
                ", path='" + path + '\'' +
                ", commitId='" + commitId + '\'' +
                ", repoId='" + repoId + '\'' +
                ", login='" + login + '\'' +
                ", gistId='" + gistId + '\'' +
                ", issueId='" + issueId + '\'' +
                ", pullRequestId='" + pullRequestId + '\'' +
                ", reactions=" + reactions +
                '}'
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeParcelable(user, flags)
        dest.writeString(url)
        dest.writeString(body)
        dest.writeString(bodyHtml)
        dest.writeString(htmlUrl)
        dest.writeLong(if (createdAt != null) createdAt!!.time else -1)
        dest.writeLong(if (updatedAt != null) updatedAt!!.time else -1)
        dest.writeInt(position)
        dest.writeInt(line)
        dest.writeString(path)
        dest.writeString(commitId)
        dest.writeString(repoId)
        dest.writeString(login)
        dest.writeString(gistId)
        dest.writeString(issueId)
        dest.writeString(pullRequestId)
        dest.writeParcelable(reactions, flags)
    }

    private constructor(`in`: Parcel) {
        id = `in`.readLong()
        user = `in`.readParcelable(User::class.java.classLoader)
        url = `in`.readString()
        body = `in`.readString()
        bodyHtml = `in`.readString()
        htmlUrl = `in`.readString()
        val tmpCreatedAt = `in`.readLong()
        createdAt = if (tmpCreatedAt == -1L) null else Date(tmpCreatedAt)
        val tmpUpdatedAt = `in`.readLong()
        updatedAt = if (tmpUpdatedAt == -1L) null else Date(tmpUpdatedAt)
        position = `in`.readInt()
        line = `in`.readInt()
        path = `in`.readString()
        commitId = `in`.readString()
        repoId = `in`.readString()
        login = `in`.readString()
        gistId = `in`.readString()
        issueId = `in`.readString()
        pullRequestId = `in`.readString()
        reactions = `in`.readParcelable(ReactionsModel::class.java.classLoader)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CommentEvent> = object : Parcelable.Creator<CommentEvent> {
            override fun createFromParcel(source: Parcel): CommentEvent {
                return CommentEvent(source)
            }

            override fun newArray(size: Int): Array<CommentEvent?> {
                return arrayOfNulls(size)
            }
        }
    }
}
