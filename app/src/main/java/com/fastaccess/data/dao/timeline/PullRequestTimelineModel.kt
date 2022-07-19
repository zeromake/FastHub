package com.fastaccess.data.dao.timeline

import com.fastaccess.data.dao.ReactionsModel
import com.fastaccess.data.dao.timeline.PullRequestReviewModel.Companion.build
import com.fastaccess.data.entity.PullRequest
import com.fastaccess.github.PullRequestTimelineQuery

/**
 * Created by kosh on 02/08/2017.
 */
class PullRequestTimelineModel {
    var node: PullRequestTimelineQuery.Node? = null
    var pullRequest: PullRequest? = null
    var status: PullRequestTimelineQuery.Status? = null
    var reactions: List<ReactionsModel>? = null
    var isMergeable = false
    var commitThread: PullRequestCommitModel? = null
    var reviewModel: PullRequestReviewModel? = null

    constructor(pullRequest: PullRequest?) {
        this.pullRequest = pullRequest
    }

    constructor(node: PullRequestTimelineQuery.Node) {
        this.node = node
        if (this.node?.onCommitCommentThread == null) {
            if (node.onPullRequestReview != null || node.onReviewDismissedEvent != null || node.onReviewRequestedEvent != null || node.onReviewRequestRemovedEvent != null) {
                reviewModel = build(node)
            }
        }
    }

    constructor(status: PullRequestTimelineQuery.Status?, isMergeable: Boolean) {
        this.status = status
        this.isMergeable = isMergeable
    }

    val type: Int
        get() {
            if (pullRequest != null) return HEADER
            if (node != null) {
                if (node!!.onAssignedEvent != null || node!!.onClosedEvent != null || node!!.onDemilestonedEvent != null || node!!.onHeadRefDeletedEvent != null || node!!.onLabeledEvent != null || node!!.onLockedEvent != null || node!!.onMergedEvent != null || node!!.onMilestonedEvent != null || node!!.onReferencedEvent != null || node!!.onRenamedTitleEvent != null || node!!.onReopenedEvent != null || node!!.onUnassignedEvent != null || node!!.onUnlabeledEvent != null || node!!.onUnlockedEvent != null || node!!.onCommit != null || node!!.onHeadRefRestoredEvent != null) {
                    return EVENT
                } else if (node!!.onIssueComment != null) {
                    if (reactions == null) {
                        reactions = (ReactionsModel.getReaction2(node!!.onIssueComment!!.reactionGroups))
                    }
                    return COMMENT
                } else if (reviewModel != null) {
                    return REVIEW
                } else if (commitThread != null) {
                    return COMMIT_COMMENTS
                }
            } else if (status != null) {
                return STATUS
            }
            return 0
        }

    override fun toString(): String {
        return type.toString()
    }

    companion object {
        const val HEADER = 1
        const val EVENT = 2
        const val COMMENT = 3
        const val STATUS = 4
        const val REVIEW = 5
        const val COMMIT_COMMENTS = 6
    }
}
