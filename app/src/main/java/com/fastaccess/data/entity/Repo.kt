package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.LicenseModel
import com.fastaccess.data.dao.RepoPermissionsModel
import com.fastaccess.data.dao.TopicsModel
import com.fastaccess.data.entity.converters.*
import com.fastaccess.helper.readAtCompat
import com.fastaccess.helper.readBooleanCompat
import com.fastaccess.helper.writeAtCompat
import com.fastaccess.helper.writeBooleanCompat
import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.NameInDb
import java.util.*

@Entity
data class Repo(
    @Id(assignable = true)
    var id: Long = 0,
    var name: String? = null,
    var fullName: String? = null,
    @SerializedName("private")
    var privateX: Boolean = false,
    var htmlUrl: String? = null,
    var description: String? = null,
    var fork: Boolean = false,
    var url: String? = null,
    var forksUrl: String? = null,
    var keysUrl: String? = null,
    var collaboratorsUrl: String? = null,
    var teamsUrl: String? = null,
    var hooksUrl: String? = null,
    var issueEventsUrl: String? = null,
    var eventsUrl: String? = null,
    var assigneesUrl: String? = null,
    var branchesUrl: String? = null,
    var tagsUrl: String? = null,
    var blobsUrl: String? = null,
    var gitTagsUrl: String? = null,
    var gitRefsUrl: String? = null,
    var treesUrl: String? = null,
    var statusesUrl: String? = null,
    var languagesUrl: String? = null,
    var stargazersUrl: String? = null,
    var contributorsUrl: String? = null,
    var subscribersUrl: String? = null,
    var subscriptionUrl: String? = null,
    var commitsUrl: String? = null,
    var gitCommitsUrl: String? = null,
    var commentsUrl: String? = null,
    var issueCommentUrl: String? = null,
    var contentsUrl: String? = null,
    var compareUrl: String? = null,
    var mergesUrl: String? = null,
    var archiveUrl: String? = null,
    var downloadsUrl: String? = null,
    var issuesUrl: String? = null,
    var pullsUrl: String? = null,
    var milestonesUrl: String? = null,
    var notificationsUrl: String? = null,
    var labelsUrl: String? = null,
    var releasesUrl: String? = null,
    var createdAt: Date? = null,
    var updatedAt: Date? = null,
    var pushedAt: Date? = null,
    var gitUrl: String? = null,
    var sshUrl: String? = null,
    var cloneUrl: String? = null,
    var svnUrl: String? = null,
    var homepage: String? = null,
    var size: Long = 0,
    var stargazersCount: Long = 0,
    var watchersCount: Long = 0,

    @NameInDb("language_column")
    var language: String? = null,
    var hasIssues: Boolean = false,
    var hasDownloads: Boolean = false,
    var hasWiki: Boolean = false,
    var hasPages: Boolean = false,
    var forksCount: Long = 0,
    var mirrorUrl: String? = null,
    var openIssuesCount: Long = 0,
    var forks: Long = 0,
    var openIssues: Long = 0,
    var watchers: Long = 0,
    var defaultBranch: String? = null,

    @Convert(converter = TopicsConverter::class, dbType = String::class)
    var topics: TopicsModel? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var owner: User? = null,

    @Convert(converter = RepoPermissionConverter::class, dbType = String::class)
    var permissions: RepoPermissionsModel? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var organization: User? = null,

    @Convert(converter = RepoConverter::class, dbType = String::class)
    var parent: Repo? = null,

    @Convert(converter = RepoConverter::class, dbType = String::class)
    var source: Repo? = null,

    @Convert(converter = LicenseConverter::class, dbType = String::class)
    var license: LicenseModel? = null,

    @SerializedName("subscribers_count")
    var subsCount: Int = 0,
    var networkCount: Int = 0,
    var starredUser: String? = null,
    var reposOwner: String? = null,

    var hasProjects: Boolean = false,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBooleanCompat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBooleanCompat(),
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
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readAtCompat(),
        parcel.readAtCompat(),
        parcel.readAtCompat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readBooleanCompat(),
        parcel.readBooleanCompat(),
        parcel.readBooleanCompat(),
        parcel.readBooleanCompat(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString(),
        fun(): TopicsModel {
            val topics = TopicsModel()
            parcel.readList(topics, TopicsModel::class.java.classLoader)
            return topics
        }(),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(RepoPermissionsModel::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(Repo::class.java.classLoader),
        parcel.readParcelable(Repo::class.java.classLoader),
        parcel.readParcelable(LicenseModel::class.java.classLoader),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readBooleanCompat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(name)
        parcel.writeString(fullName)
        parcel.writeBooleanCompat(privateX)
        parcel.writeString(htmlUrl)
        parcel.writeString(description)
        parcel.writeBooleanCompat(fork)
        parcel.writeString(url)
        parcel.writeString(forksUrl)
        parcel.writeString(keysUrl)
        parcel.writeString(collaboratorsUrl)
        parcel.writeString(teamsUrl)
        parcel.writeString(hooksUrl)
        parcel.writeString(issueEventsUrl)
        parcel.writeString(eventsUrl)
        parcel.writeString(assigneesUrl)
        parcel.writeString(branchesUrl)
        parcel.writeString(tagsUrl)
        parcel.writeString(blobsUrl)
        parcel.writeString(gitTagsUrl)
        parcel.writeString(gitRefsUrl)
        parcel.writeString(treesUrl)
        parcel.writeString(statusesUrl)
        parcel.writeString(languagesUrl)
        parcel.writeString(stargazersUrl)
        parcel.writeString(contributorsUrl)
        parcel.writeString(subscribersUrl)
        parcel.writeString(subscriptionUrl)
        parcel.writeString(commitsUrl)
        parcel.writeString(gitCommitsUrl)
        parcel.writeString(commentsUrl)
        parcel.writeString(issueCommentUrl)
        parcel.writeString(contentsUrl)
        parcel.writeString(compareUrl)
        parcel.writeString(mergesUrl)
        parcel.writeString(archiveUrl)
        parcel.writeString(downloadsUrl)
        parcel.writeString(issuesUrl)
        parcel.writeString(pullsUrl)
        parcel.writeString(milestonesUrl)
        parcel.writeString(notificationsUrl)
        parcel.writeString(labelsUrl)
        parcel.writeAtCompat(createdAt)
        parcel.writeAtCompat(updatedAt)
        parcel.writeAtCompat(pushedAt)
        parcel.writeString(releasesUrl)
        parcel.writeString(gitUrl)
        parcel.writeString(sshUrl)
        parcel.writeString(cloneUrl)
        parcel.writeString(svnUrl)
        parcel.writeString(homepage)
        parcel.writeLong(size)
        parcel.writeLong(stargazersCount)
        parcel.writeLong(watchersCount)
        parcel.writeString(language)
        parcel.writeBooleanCompat(hasIssues)
        parcel.writeBooleanCompat(hasDownloads)
        parcel.writeBooleanCompat(hasWiki)
        parcel.writeBooleanCompat(hasPages)
        parcel.writeLong(forksCount)
        parcel.writeString(mirrorUrl)
        parcel.writeLong(openIssuesCount)
        parcel.writeLong(forks)
        parcel.writeLong(openIssues)
        parcel.writeLong(watchers)
        parcel.writeString(defaultBranch)
        parcel.writeList(topics)
        parcel.writeParcelable(owner, flags)
        parcel.writeParcelable(permissions, flags)
        parcel.writeParcelable(organization, flags)
        parcel.writeParcelable(parent, flags)
        parcel.writeParcelable(source, flags)
        parcel.writeParcelable(license, flags)
        parcel.writeInt(subsCount)
        parcel.writeInt(networkCount)
        parcel.writeString(starredUser)
        parcel.writeString(reposOwner)
        parcel.writeBooleanCompat(hasProjects)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Repo> {
        override fun createFromParcel(parcel: Parcel): Repo {
            return Repo(parcel)
        }

        override fun newArray(size: Int): Array<Repo?> {
            return arrayOfNulls(size)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as Repo
        return id == that.id
    }

    override fun hashCode(): Int {
        return (id xor (id ushr 32)).toInt()
    }
}