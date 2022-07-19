package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.ReactionsModel
import com.fastaccess.data.entity.converters.ReactionsConverter
import com.fastaccess.data.entity.converters.UserConverter
import com.fastaccess.helper.readAtCompat
import com.fastaccess.helper.writeAtCompat
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.NameInDb
import java.util.*

@Entity
data class Comment(
    @Id(assignable = true)
    var id: Long = 0,
    @NameInDb("user_column")
    @Convert(converter = UserConverter::class, dbType = String::class)
    var user: User? = null,
    var url: String? = null,
    var body: String? = null,
    var bodyHtml: String? = null,
    var htmlUrl: String? = null,
    var createdAt: Date? = null,
    var updatedAt: Date? = null,
    var position: Int = 0,
    var line: Int = 0,
    var path: String? = null,
    var commitId: String? = null,
    var repoId: String? = null,
    var login: String? = null,
    var gistId: String? = null,
    var issueId: String? = null,
    var pullRequestId: String? = null,

    @Convert(converter = ReactionsConverter::class, dbType = String::class)
    var reactions: ReactionsModel? = null,
    var authorAssociation: String? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readAtCompat(),
        parcel.readAtCompat(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(ReactionsModel::class.java.classLoader),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeParcelable(user, flags)
        parcel.writeString(url)
        parcel.writeString(body)
        parcel.writeString(bodyHtml)
        parcel.writeString(htmlUrl)
        parcel.writeAtCompat(createdAt)
        parcel.writeAtCompat(updatedAt)
        parcel.writeInt(position)
        parcel.writeInt(line)
        parcel.writeString(path)
        parcel.writeString(commitId)
        parcel.writeString(repoId)
        parcel.writeString(login)
        parcel.writeString(gistId)
        parcel.writeString(issueId)
        parcel.writeString(pullRequestId)
        parcel.writeParcelable(reactions, flags)
        parcel.writeString(authorAssociation)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}
