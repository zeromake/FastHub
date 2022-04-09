package com.fastaccess.data.dao

import android.os.Parcelable
import com.fastaccess.data.dao.types.IssueEventType
import com.fastaccess.data.dao.timeline.GenericEvent
import com.fastaccess.data.dao.timeline.PullRequestCommitModel
import android.os.Parcel
import com.fastaccess.data.entity.Comment
import com.fastaccess.data.entity.Issue
import com.fastaccess.data.entity.PullRequest
import io.reactivex.Observable

/**
 * Created by Kosh on 30 Mar 2017, 9:03 PM
 */
open class TimelineModel : Parcelable {
    var event: IssueEventType? = null
    var comment: Comment? = null
    var genericEvent: GenericEvent? = null
    var status: PullRequestStatusModel? = null
    var issue: Issue? = null
    var pullRequest: PullRequest? = null
    var review: ReviewModel? = null
    var groupedReviewModel: GroupedReviewModel? = null
    var commit: PullRequestCommitModel? = null
    var position = 0

    constructor(issue: Issue?) {
        this.issue = issue
    }

    constructor(pullRequest: PullRequest?) {
        this.pullRequest = pullRequest
    }

    constructor(comment: Comment?) {
        this.comment = comment
        event = IssueEventType.commented
    }

    constructor(statusModel: PullRequestStatusModel?) {
        status = statusModel
    }

    constructor() {}

    val type: Int
        get() = if (event != null) {
            when (event) {
                IssueEventType.commented -> COMMENT
                IssueEventType.reviewed, IssueEventType.changes_requested -> REVIEW
                IssueEventType.GROUPED -> GROUP
                IssueEventType.commit_commented -> COMMIT_COMMENTS
                else -> EVENT
            }
        } else {
            if (issue != null || pullRequest != null) HEADER else if (status != null) STATUS else 0
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val model = other as TimelineModel
        return comment != null && model.comment != null && comment!!.id == model.comment!!.id
    }

    override fun hashCode(): Int {
        return if (comment != null) comment!!.id.toInt() else 0
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(if (event == null) -1 else event!!.ordinal)
        dest.writeParcelable(comment, flags)
        dest.writeParcelable(genericEvent, flags)
        dest.writeParcelable(status, flags)
        dest.writeParcelable(issue, flags)
        dest.writeParcelable(pullRequest, flags)
        dest.writeParcelable(review, flags)
        dest.writeParcelable(groupedReviewModel, flags)
        dest.writeParcelable(commit, flags)
        dest.writeInt(position)
    }

    protected constructor(`in`: Parcel) {
        val tmpEvent = `in`.readInt()
        event = if (tmpEvent == -1) null else IssueEventType.values()[tmpEvent]
        comment = `in`.readParcelable(Comment::class.java.classLoader)
        genericEvent = `in`.readParcelable(GenericEvent::class.java.classLoader)
        status = `in`.readParcelable(PullRequestStatusModel::class.java.classLoader)
        issue = `in`.readParcelable(Issue::class.java.classLoader)
        pullRequest = `in`.readParcelable(PullRequest::class.java.classLoader)
        review = `in`.readParcelable(ReviewModel::class.java.classLoader)
        groupedReviewModel = `in`.readParcelable(GroupedReviewModel::class.java.classLoader)
        commit = `in`.readParcelable(PullRequestCommitModel::class.java.classLoader)
        position = `in`.readInt()
    }

    companion object {
        const val HEADER = 1
        const val EVENT = 2
        const val COMMENT = 3
        const val REVIEW = 4
        const val GROUP = 5
        const val COMMIT_COMMENTS = 6
        const val STATUS = 7
        @JvmStatic
        fun constructHeader(issue: Issue?): TimelineModel {
            return TimelineModel(issue)
        }

        @JvmStatic
        fun constructHeader(pullRequest: PullRequest?): TimelineModel {
            return TimelineModel(pullRequest)
        }

        @JvmStatic
        fun constructComment(comment: Comment?): TimelineModel {
            return TimelineModel(comment)
        }

        @JvmStatic
        fun construct(comments: List<Comment>?): Observable<List<TimelineModel>> {
            return if (comments == null || comments.isEmpty()) Observable.empty() else Observable.fromIterable(
                comments
            )
                .map { comment: Comment? -> TimelineModel(comment) }
                .toList()
                .toObservable()
        }

        @JvmField
        val CREATOR: Parcelable.Creator<TimelineModel> =
            object : Parcelable.Creator<TimelineModel> {
                override fun createFromParcel(source: Parcel): TimelineModel {
                    return TimelineModel(source)
                }

                override fun newArray(size: Int): Array<TimelineModel?> {
                    return arrayOfNulls(size)
                }
            }
    }
}