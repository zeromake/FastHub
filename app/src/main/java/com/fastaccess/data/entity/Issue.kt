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
import com.fastaccess.helper.readList
import com.fastaccess.helper.writeBooleanCompat
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.NameInDb
import java.util.*

@Entity
data class Issue(
    @Id(assignable = true)
    var id: Long = 0,
    var url: String? = null,
    var body: String? = null,
    var title: String? = null,
    var comments: Int = 0,
    var number: Int = 0,
    var locked: Boolean = false,
    @Convert(converter = IssueStateConverter::class, dbType = Int::class)
    var state: IssueState? = null,
    var repoUrl: String? = null,
    var bodyHtml: String? = null,
    var htmlUrl: String? = null,
    var closedAt: Date? = null,
    var createdAt: Date? = null,
    var updatedAt: Date? = null,
    var repoId: String? = null,
    var login: String? = null,
    @NameInDb("user_column")
    @Convert(converter = UserConverter::class, dbType = String::class)
    var user: User? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var assignee: User? = null,

    @Convert(converter = UsersConverter::class, dbType = String::class)
    var assignees: UsersListModel? = null,

    @Convert(converter = LabelsListConverter::class, dbType = String::class)
    var labels: LabelListModel? = null,

    @Convert(converter = MilestoneConverter::class, dbType = String::class)
    var milestone: MilestoneModel? = null,

    @Convert(converter = RepoConverter::class, dbType = String::class)
    var repository: Repo? = null,

    @Convert(converter = PullRequestConverter::class, dbType = String::class)
    var pullRequest: PullRequest? = null,

    @Convert(converter = UserConverter::class, dbType = String::class)
    var closedBy: User? = null,

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
        fun(): IssueState? {
            val state = parcel.readInt()
            return if (state == -1) null else IssueState.values()[state]
        }(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readAtCompat(),
        parcel.readAtCompat(),
        parcel.readAtCompat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
        parcel.readList { UsersListModel() },
        parcel.readList { LabelListModel() },
        parcel.readParcelable(MilestoneModel::class.java.classLoader),
        parcel.readParcelable(Repo::class.java.classLoader),
        parcel.readParcelable(PullRequest::class.java.classLoader),
        parcel.readParcelable(User::class.java.classLoader),
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
        parcel.writeInt(state?.ordinal ?: -1)
        parcel.writeString(repoUrl)
        parcel.writeString(bodyHtml)
        parcel.writeString(htmlUrl)
        parcel.writeString(repoId)
        parcel.writeString(login)
        parcel.writeParcelable(user, flags)
        parcel.writeParcelable(assignee, flags)
        parcel.writeList(assignees)
        parcel.writeList(labels)
        parcel.writeParcelable(milestone, flags)
        parcel.writeParcelable(repository, flags)
        parcel.writeParcelable(pullRequest, flags)
        parcel.writeParcelable(closedBy, flags)
        parcel.writeParcelable(reactions, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Issue> {
        override fun createFromParcel(parcel: Parcel): Issue {
            return Issue(parcel)
        }

        override fun newArray(size: Int): Array<Issue?> {
            return arrayOfNulls(size)
        }
    }
}
