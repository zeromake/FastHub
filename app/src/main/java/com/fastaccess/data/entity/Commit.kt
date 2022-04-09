package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.CommitFileListModel
import com.fastaccess.data.dao.CommitListModel
import com.fastaccess.data.dao.GitCommitModel
import com.fastaccess.data.dao.GithubState
import com.fastaccess.data.entity.converters.*
import com.fastaccess.helper.readBooleanCompat
import com.fastaccess.helper.writeBooleanCompat
import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.NameInDb

@Entity
@NameInDb("commit_table")
data class Commit(
    @Id
    var id: Long = 0,
    var url: String? = null,
    var sha: String? = null,
    var htmlUrl: String? = null,
    var login: String? = null,
    var repoId: String? = null,
    var pullRequestNumber: Long = 0,

    @Convert(converter = GitHubStateConverter::class, dbType = String::class)
    var stats: GithubState? = null,

    @Convert(converter = CommitFilesConverter::class, dbType = String::class)
    var files: CommitFileListModel? = null,

    @Convert(converter = CommitsConverter::class, dbType = String::class)
    var parents: CommitListModel? = null,

    @NameInDb("ref_column")
    var ref: String? = null,

    @SerializedName("distincted")
    var distincted: Boolean = false,

    @SerializedName("commit")
    @Convert(converter = GitCommitConverter::class, dbType = String::class)
    var gitCommit: GitCommitModel? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var author: User? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var committer: User? = null,

    @Convert(converter = RepoConverter::class, dbType = String::class)
    var repo: Repo? = null,

    @NameInDb("user_column")
    @Convert(converter = UserConverter::class, dbType = String::class)
    var user: User? = null,

    var commentCount: Int = 0,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readParcelable(GithubState::class.java.classLoader),
        fun(): CommitFileListModel {
            val files = CommitFileListModel()
            parcel.readList(files, CommitFileListModel::class.java.classLoader)
            return files
        }(),
        fun(): CommitListModel {
            val parents = CommitListModel()
            parcel.readList(parents, CommitListModel::class.java.classLoader)
            return parents
        }(),
        parcel.readString(),
        parcel.readBooleanCompat(),
        parcel.readParcelable(GitCommitModel::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(Repo::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(url)
        parcel.writeString(sha)
        parcel.writeString(htmlUrl)
        parcel.writeString(login)
        parcel.writeString(repoId)
        parcel.writeLong(pullRequestNumber)
        parcel.writeParcelable(stats, flags)
        parcel.writeList(files)
        parcel.writeList(parents)
        parcel.writeString(ref)
        parcel.writeBooleanCompat(distincted)
        parcel.writeParcelable(gitCommit, flags)
        parcel.writeParcelable(author, flags)
        parcel.writeParcelable(committer, flags)
        parcel.writeParcelable(repo, flags)
        parcel.writeParcelable(user, flags)
        parcel.writeInt(commentCount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Commit> {
        override fun createFromParcel(parcel: Parcel): Commit {
            return Commit(parcel)
        }

        override fun newArray(size: Int): Array<Commit?> {
            return arrayOfNulls(size)
        }
    }
}
