package com.fastaccess.data.entity

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.LabelListModel
import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.data.dao.ReactionsModel
import com.fastaccess.data.dao.UsersListModel
import com.fastaccess.data.dao.types.IssueState
import com.fastaccess.data.entity.converters.*
import com.fastaccess.helper.readAtCompat
import com.fastaccess.helper.readBooleanCompat
import com.fastaccess.helper.writeAtCompat
import com.fastaccess.helper.writeBooleanCompat
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.NameInDb
import java.util.*

@Entity
data class PullRequest(
    @Id(assignable = true)
    var id: Long = 0,
    var url: String? = null,
    var body: String? = null,
    var title: String? = null,
    var comments: Int = 0,
    var number: Int = 0,
    var locked: Boolean = false,
    var mergable: Boolean = false,
    var merged: Boolean = false,
    var mergeable: Boolean = false,
    var commits: Int = 0,
    var additions: Int = 0,
    var deletions: Int = 0,
    @Convert(converter = IssueStateConverter::class, dbType = Int::class)
    var state: IssueState? = null,
    var bodyHtml: String? = null,
    var htmlUrl: String? = null,
    var closedAt: Date? = null,
    var createdAt: Date? = null,
    var updatedAt: Date? = null,
    var changedFiles: Int = 0,
    var diffUrl: String? = null,
    var patchUrl: String? = null,
    var mergeCommitSha: String? = null,
    var mergedAt: Date? = null,
    var mergeState: String? = null,
    var reviewComments: Int = 0,
    var repoId: String? = null,
    var login: String? = null,
    var mergeableState: String? = null,

    @Convert(converter = UsersConverter::class, dbType = String::class)
    var assignees: UsersListModel? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var mergedBy: User? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var closedBy: User? = null,

    @NameInDb("user_column")
    @Convert(converter = UserConverter::class, dbType = String::class)
    var user: User? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var assignee: User? = null,

    @Convert(converter = LabelsListConverter::class, dbType = String::class)
    var labels: LabelListModel? = null,

    @Convert(converter = MilestoneConverter::class, dbType = String::class)
    var milestone: MilestoneModel? = null,

    @Convert(converter = CommitConverter::class, dbType = String::class)
    var base: Commit? = null,

    @Convert(converter = CommitConverter::class, dbType = String::class)
    var head: Commit? = null,

    @Convert(converter = PullRequestConverter::class, dbType = String::class)
    var pullRequest: PullRequest? = null,

    @Convert(converter = ReactionsConverter::class, dbType = String::class)
    var reactions: ReactionsModel? = null,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readBooleanCompat(),
        parcel.readBooleanCompat(),
        parcel.readBooleanCompat(),
        parcel.readBooleanCompat(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        fun(): IssueState? {
            val state = parcel.readInt()
            return if (state == -1) null else IssueState.values()[state]
        }(),
        parcel.readString(),
        parcel.readString(),
        parcel.readAtCompat(),
        parcel.readAtCompat(),
        parcel.readAtCompat(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readAtCompat(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        fun(): UsersListModel {
            val assignees = UsersListModel()
            parcel.readList(assignees, UsersListModel::class.java.classLoader)
            return assignees
        }(),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        fun(): LabelListModel {
            val labels = LabelListModel()
            parcel.readList(labels, LabelListModel::class.java.classLoader)
            return labels
        }(),
        parcel.readParcelable(MilestoneModel::class.java.classLoader),
        parcel.readParcelable(Commit::class.java.classLoader),
        parcel.readParcelable(Commit::class.java.classLoader),
        parcel.readParcelable(PullRequest::class.java.classLoader),
        parcel.readParcelable(ReactionsModel::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(url)
        parcel.writeString(body)
        parcel.writeString(title)
        parcel.writeInt(comments)
        parcel.writeInt(number)
        parcel.writeBooleanCompat(locked)
        parcel.writeBooleanCompat(mergable)
        parcel.writeBooleanCompat(merged)
        parcel.writeBooleanCompat(mergeable)
        parcel.writeInt(commits)
        parcel.writeInt(additions)
        parcel.writeInt(deletions)
        parcel.writeInt(state?.ordinal ?: -1)
        parcel.writeString(bodyHtml)
        parcel.writeString(htmlUrl)
        parcel.writeAtCompat(closedAt)
        parcel.writeAtCompat(createdAt)
        parcel.writeAtCompat(updatedAt)
        parcel.writeInt(changedFiles)
        parcel.writeString(diffUrl)
        parcel.writeString(patchUrl)
        parcel.writeString(mergeCommitSha)
        parcel.writeAtCompat(mergedAt)
        parcel.writeString(mergeState)
        parcel.writeInt(reviewComments)
        parcel.writeString(repoId)
        parcel.writeString(login)
        parcel.writeString(mergeableState)
        parcel.writeList(assignees)
        parcel.writeParcelable(mergedBy, flags)
        parcel.writeParcelable(closedBy, flags)
        parcel.writeParcelable(user, flags)
        parcel.writeParcelable(assignee, flags)
        parcel.writeList(labels)
        parcel.writeParcelable(milestone, flags)
        parcel.writeParcelable(base, flags)
        parcel.writeParcelable(head, flags)
        parcel.writeParcelable(pullRequest, flags)
        parcel.writeParcelable(reactions, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PullRequest> {
        override fun createFromParcel(parcel: Parcel): PullRequest {
            return PullRequest(parcel)
        }

        override fun newArray(size: Int): Array<PullRequest?> {
            return arrayOfNulls(size)
        }
    }
}
