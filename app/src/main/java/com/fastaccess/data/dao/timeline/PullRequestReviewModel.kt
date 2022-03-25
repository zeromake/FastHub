package com.fastaccess.data.dao.timeline

import com.fastaccess.data.dao.ReactionsModel
import com.fastaccess.github.PullRequestTimelineQuery
import com.fastaccess.github.type.PullRequestReviewState
import com.fastaccess.helper.Logger
import com.fastaccess.helper.ParseDateFormat

/**
 * Created by kosh on 20/08/2017.
 */
class PullRequestReviewModel {
    var reviewDismissedEvent: PullRequestTimelineQuery.OnReviewDismissedEvent? = null
    var reviewRequestedEvent: PullRequestTimelineQuery.OnReviewRequestedEvent? = null
    var reviewRequestRemovedEvent: PullRequestTimelineQuery.OnReviewRequestRemovedEvent? = null
    var node: PullRequestTimelineQuery.Node2? = null
    var reaction: List<ReactionsModel>? = null
    var comments: List<PullRequestReviewModel>? = null
    var id: String? = null
    var url: String? = null
    var author: PullRequestTimelineQuery.Author2? = null
    var bodyHTML: String? = null
    var createdAt: String? = null
    var state: PullRequestReviewState? = null

    companion object {
        @JvmStatic
        fun build(node: PullRequestTimelineQuery.Node): PullRequestReviewModel? {
            val model = PullRequestReviewModel()
            if (node.onReviewRequestRemovedEvent != null) {
                model.reviewRequestRemovedEvent = node.onReviewRequestRemovedEvent
            } else if (node.onReviewDismissedEvent != null) {
                model.reviewDismissedEvent = node.onReviewDismissedEvent
            } else if (node.onReviewRequestedEvent != null) {
                model.reviewRequestedEvent = node.onReviewRequestedEvent
            } else {
                val pullRequestReview = node.onPullRequestReview
                if (pullRequestReview != null) {
                    model.state = pullRequestReview.state
                    model.url = pullRequestReview.url.toString()
                    model.author = pullRequestReview.author
                    model.bodyHTML = pullRequestReview.bodyHTML.toString()
                    model.createdAt =
                        ParseDateFormat.getTimeAgo(pullRequestReview.createdAt.toString())
                            .toString()
                    model.id = pullRequestReview.id
                    model.url = pullRequestReview.url.toString()
                    val edges = pullRequestReview.comments.edges
                    if (edges != null && edges.isNotEmpty()) {
                        val comments: MutableList<PullRequestReviewModel> = ArrayList()
                        for (edge in edges) {
                            val node2 = edge?.node
                            if (node2 != null) {
                                val comment = PullRequestReviewModel()
                                comment.node = node2
                                comment.reaction = ReactionsModel.getReaction(node2.reactionGroups)
                                comments.add(comment)
                            }
                        }
                        Logger.e(comments.size)
                        model.comments = comments
                    }
                } else {
                    return null
                }
            }
            return model
        }
    }
}