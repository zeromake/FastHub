package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.helper.readAtCompat
import com.fastaccess.helper.readBooleanCompat
import com.fastaccess.helper.writeAtCompat
import com.fastaccess.helper.writeBooleanCompat
import io.objectbox.annotation.*
import java.util.Date

@Entity
@NameInDb("user_table")
data class User(
    @Id(assignable = true)
    var id: Long = 0,
    @Index
    var login: String? = null,
    var avatarUrl: String? = null,
    var gravatarId: String? = null,
    var url: String? = null,
    var htmlUrl: String? = null,
    var followersUrl: String? = null,
    var followingUrl: String? = null,
    var gistsUrl: String? = null,
    var starredUrl: String? = null,
    var subscriptionsUrl: String? = null,
    var organizationsUrl: String? = null,
    var reposUrl: String? = null,
    var eventsUrl: String? = null,
    var receivedEventsUrl: String? = null,
    var type: String? = null,
    var siteAdmin: Boolean = false,
    var name: String? = null,
    var company: String? = null,
    var blog: String? = null,
    var location: String? = null,
    var email: String? = null,
    var hireable: Boolean = false,
    var bio: String? = null,
    var publicRepos: Long = 0,
    var publicGists: Long = 0,
    var followers: Long = 0,
    var following: Long = 0,
    var createdAt: Date? = null,
    var updatedAt: Date? = null,
    var contributions: Int = 0,
    var followingName: String? = null,
    var followerName: String? = null,

    @NameInDb("date_column")
    var date: Date? = null,
    var repoId: String? = null,
    var description: String? = null,
) : Parcelable {
    val isOrganizationType: Boolean
        get() = type != null && type.equals("Organization", true)

    var hasOrganizationProjects: Boolean = false
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBooleanCompat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBooleanCompat(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readAtCompat(),
        parcel.readAtCompat(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readAtCompat(),
        parcel.readString(),
        parcel.readString(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(login)
        parcel.writeString(avatarUrl)
        parcel.writeString(gravatarId)
        parcel.writeString(url)
        parcel.writeString(htmlUrl)
        parcel.writeString(followersUrl)
        parcel.writeString(followingUrl)
        parcel.writeString(gistsUrl)
        parcel.writeString(starredUrl)
        parcel.writeString(subscriptionsUrl)
        parcel.writeString(organizationsUrl)
        parcel.writeString(reposUrl)
        parcel.writeString(eventsUrl)
        parcel.writeString(receivedEventsUrl)
        parcel.writeString(type)
        parcel.writeBooleanCompat(siteAdmin)
        parcel.writeString(name)
        parcel.writeString(company)
        parcel.writeString(blog)
        parcel.writeString(location)
        parcel.writeString(email)
        parcel.writeBooleanCompat(hireable)
        parcel.writeString(bio)
        parcel.writeLong(publicRepos)
        parcel.writeLong(publicGists)
        parcel.writeLong(followers)
        parcel.writeLong(following)
        parcel.writeAtCompat(createdAt)
        parcel.writeAtCompat(updatedAt)
        parcel.writeInt(contributions)
        parcel.writeString(followingName)
        parcel.writeString(followerName)
        parcel.writeString(repoId)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
