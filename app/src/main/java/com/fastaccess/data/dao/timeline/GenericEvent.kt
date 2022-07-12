package com.fastaccess.data.dao.timeline

import android.os.Parcel
import android.os.Parcelable
import com.fastaccess.data.dao.LabelModel
import com.fastaccess.data.dao.MilestoneModel
import com.fastaccess.data.dao.RenameModel
import com.fastaccess.data.dao.TeamsModel
import com.fastaccess.data.dao.types.IssueEventType
import com.fastaccess.data.entity.Issue
import com.fastaccess.data.entity.PullRequest
import com.fastaccess.data.entity.User
import java.util.*

/**
 * Created by kosh on 25/07/2017.
 */
class GenericEvent : Parcelable {
    var id: Long = 0
    var url: String? = null
    var commitId: String? = null
    var commitUrl: String? = null
    var message: String? = null
    var sha: String? = null
    var htmlUrl: String? = null
    var createdAt: Date? = null
    var actor: User? = null
    var requestedReviewer: User? = null
    var reviewRequester: User? = null
    var assigner: User? = null
    var assignee: User? = null
    var author: User? = null
    var committer: User? = null
    var label: LabelModel? = null
    var requestedTeam: TeamsModel? = null
    var milestone: MilestoneModel? = null
    var rename: RenameModel? = null
    var source: SourceModel? = null
    var issue: Issue? = null
    var pullRequest: PullRequest? = null
    var tree: ParentsModel? = null
    var parents: List<ParentsModel>? = null
    var event: IssueEventType? = null
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeString(url)
        dest.writeString(commitId)
        dest.writeString(commitUrl)
        dest.writeString(message)
        dest.writeString(sha)
        dest.writeString(htmlUrl)
        dest.writeLong(if (createdAt != null) createdAt!!.time else -1)
        dest.writeParcelable(actor, flags)
        dest.writeParcelable(requestedReviewer, flags)
        dest.writeParcelable(reviewRequester, flags)
        dest.writeParcelable(assigner, flags)
        dest.writeParcelable(assignee, flags)
        dest.writeParcelable(author, flags)
        dest.writeParcelable(committer, flags)
        dest.writeParcelable(label, flags)
        dest.writeParcelable(requestedTeam, flags)
        dest.writeParcelable(milestone, flags)
        dest.writeParcelable(rename, flags)
        dest.writeParcelable(source, flags)
        dest.writeParcelable(issue, flags)
        dest.writeParcelable(pullRequest, flags)
        dest.writeParcelable(tree, flags)
        dest.writeTypedList(parents)
        dest.writeInt(if (event == null) -1 else event!!.ordinal)
    }

    private constructor(`in`: Parcel) {
        id = `in`.readLong()
        url = `in`.readString()
        commitId = `in`.readString()
        commitUrl = `in`.readString()
        message = `in`.readString()
        sha = `in`.readString()
        htmlUrl = `in`.readString()
        val tmpCreatedAt = `in`.readLong()
        createdAt = if (tmpCreatedAt == -1L) null else Date(tmpCreatedAt)
        actor = `in`.readParcelable(User::class.java.classLoader)
        requestedReviewer = `in`.readParcelable(User::class.java.classLoader)
        reviewRequester = `in`.readParcelable(User::class.java.classLoader)
        assigner = `in`.readParcelable(User::class.java.classLoader)
        assignee = `in`.readParcelable(User::class.java.classLoader)
        author = `in`.readParcelable(User::class.java.classLoader)
        committer = `in`.readParcelable(User::class.java.classLoader)
        label = `in`.readParcelable(LabelModel::class.java.classLoader)
        requestedTeam = `in`.readParcelable(TeamsModel::class.java.classLoader)
        milestone = `in`.readParcelable(MilestoneModel::class.java.classLoader)
        rename = `in`.readParcelable(RenameModel::class.java.classLoader)
        source = `in`.readParcelable(SourceModel::class.java.classLoader)
        issue = `in`.readParcelable(Issue::class.java.classLoader)
        pullRequest = `in`.readParcelable(PullRequest::class.java.classLoader)
        tree = `in`.readParcelable(ParentsModel::class.java.classLoader)
        parents = `in`.createTypedArrayList(ParentsModel.CREATOR)
        val tmpEvent = `in`.readInt()
        event = if (tmpEvent == -1) null else IssueEventType.values()[tmpEvent]
    }

    constructor() {}

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GenericEvent> = object : Parcelable.Creator<GenericEvent> {
            override fun createFromParcel(source: Parcel): GenericEvent {
                return GenericEvent(source)
            }

            override fun newArray(size: Int): Array<GenericEvent?> {
                return arrayOfNulls(size)
            }
        }
    }
}
