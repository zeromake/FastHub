package com.fastaccess.ui.adapter.viewholder

import android.content.res.Resources
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.data.dao.types.EventsType
import com.fastaccess.data.entity.Event
import com.fastaccess.helper.ParseDateFormat.Companion.getTimeAgo
import com.fastaccess.provider.markdown.MarkDownProvider.stripMdText
import com.fastaccess.provider.scheme.LinkParserHelper.isEnterprise
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */
class FeedsViewHolder(
    itemView: View,
    adapter: BaseRecyclerAdapter<Event, FeedsViewHolder, OnItemClickListener<Event>>?
) : BaseViewHolder<Event>(itemView, adapter) {
    private val resources: Resources = itemView.resources
    val avatar: AvatarLayout? = itemView.findViewById(R.id.avatarLayout)
    val description: FontTextView? = itemView.findViewById(R.id.description)
    val title: FontTextView? = itemView.findViewById(R.id.title)
    val date: FontTextView? = itemView.findViewById(R.id.date)
    override fun bind(t: Event) {
        appendAvatar(t)
        val spannableBuilder = builder()
        appendActor(t, spannableBuilder)
        description!!.maxLines = 2
        description.text = ""
        description.visibility = View.GONE
        if (t.type != null) {
            val type = t.type
            when {
                type === EventsType.WatchEvent -> {
                    appendWatch(spannableBuilder, type, t)
                }
                type === EventsType.CreateEvent -> {
                    appendCreateEvent(spannableBuilder, t)
                }
                type === EventsType.CommitCommentEvent -> {
                    appendCommitComment(spannableBuilder, t)
                }
                type === EventsType.DownloadEvent -> {
                    appendDownloadEvent(spannableBuilder, t)
                }
                type === EventsType.FollowEvent -> {
                    appendFollowEvent(spannableBuilder, t)
                }
                type === EventsType.ForkEvent -> {
                    appendForkEvent(spannableBuilder, t)
                }
                type === EventsType.GistEvent -> {
                    appendGistEvent(spannableBuilder, t)
                }
                type === EventsType.GollumEvent -> {
                    appendGollumEvent(spannableBuilder, t)
                }
                type === EventsType.IssueCommentEvent -> {
                    appendIssueCommentEvent(spannableBuilder, t)
                }
                type === EventsType.IssuesEvent -> {
                    appendIssueEvent(spannableBuilder, t)
                }
                type === EventsType.MemberEvent -> {
                    appendMemberEvent(spannableBuilder, t)
                }
                type === EventsType.PublicEvent -> {
                    appendPublicEvent(spannableBuilder, t)
                }
                type === EventsType.PullRequestEvent -> {
                    appendPullRequestEvent(spannableBuilder, t)
                }
                type === EventsType.PullRequestReviewCommentEvent -> {
                    appendPullRequestReviewCommentEvent(spannableBuilder, t)
                }
                type === EventsType.PullRequestReviewEvent -> {
                    appendPullRequestReviewCommentEvent(spannableBuilder, t)
                }
                type === EventsType.RepositoryEvent -> {
                    appendPublicEvent(spannableBuilder, t)
                }
                type === EventsType.PushEvent -> {
                    appendPushEvent(spannableBuilder, t)
                }
                type === EventsType.TeamAddEvent -> {
                    appendTeamEvent(spannableBuilder, t)
                }
                type === EventsType.DeleteEvent -> {
                    appendDeleteEvent(spannableBuilder, t)
                }
                type === EventsType.ReleaseEvent -> {
                    appendReleaseEvent(spannableBuilder, t)
                }
                type === EventsType.ForkApplyEvent -> {
                    appendForkApplyEvent(spannableBuilder, t)
                }
                type === EventsType.OrgBlockEvent -> {
                    appendOrgBlockEvent(spannableBuilder, t)
                }
                type === EventsType.ProjectCardEvent -> {
                    appendProjectCardEvent(spannableBuilder, t, false)
                }
                type === EventsType.ProjectColumnEvent -> {
                    appendProjectCardEvent(spannableBuilder, t, true)
                }
                type === EventsType.OrganizationEvent -> {
                    appendOrganizationEvent(spannableBuilder, t)
                }
                type === EventsType.ProjectEvent -> {
                    appendProjectCardEvent(spannableBuilder, t, false)
                }
            }
            date!!.gravity = Gravity.CENTER
            date.setEventsIcon(type!!.drawableRes)
        }
        title!!.text = spannableBuilder
        date!!.text = getTimeAgo(t.createdAt)
    }

    private fun appendOrganizationEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        spannableBuilder.bold(eventsModel.payload!!.action!!.replace("_".toRegex(), ""))
            .append(" ")
            .append(if (eventsModel.payload!!.invitation != null) eventsModel.payload!!.invitation!!.login + " " else "")
            .append(eventsModel.payload!!.organization!!.login)
    }

    private fun appendProjectCardEvent(
        spannableBuilder: SpannableBuilder,
        eventsModel: Event,
        isColumn: Boolean
    ) {
        spannableBuilder.bold(eventsModel.payload!!.action!!)
            .append(" ")
            .append(if (!isColumn) "project" else "column")
            .append(" ")
            .append(eventsModel.repo!!.name)
    }

    private fun appendOrgBlockEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        spannableBuilder.bold(eventsModel.payload!!.action!!)
            .append(" ")
            .append(eventsModel.payload!!.blockedUser!!.login)
            .append(" ")
            .append(eventsModel.payload!!.organization!!.login)
    }

    private fun appendForkApplyEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        spannableBuilder.bold(eventsModel.payload!!.head!!)
            .append(" ")
            .append(eventsModel.payload!!.before)
            .append(" ")
            .append(if (eventsModel.repo != null) "in " + eventsModel.repo!!.name else "")
    }

    private fun appendReleaseEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        val release = eventsModel.payload!!.release
        spannableBuilder.bold("released")
            .append(" ")
            .append(release!!.name)
            .append(" ")
            .append(eventsModel.repo!!.name)
    }

    private fun appendDeleteEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        spannableBuilder.bold("deleted")
            .append(" ")
            .append(eventsModel.payload!!.refType)
            .append(" ")
            .append(eventsModel.payload!!.ref)
            .append(" ")
            .bold("at")
            .append(" ")
            .append(eventsModel.repo!!.name)
    }

    private fun appendTeamEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        val teamsModel = eventsModel.payload!!.team
        val user = eventsModel.payload!!.user
        spannableBuilder.bold("added")
            .append(" ")
            .append(if (user != null) user.login else eventsModel.repo!!.name)
            .append(" ")
            .bold("in")
            .append(" ")
            .append(if (teamsModel!!.name != null) teamsModel.name else teamsModel.slug)
    }

    private fun appendPushEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        var ref = eventsModel.payload!!.ref
        if (ref!!.startsWith("refs/heads/")) {
            ref = ref.substring(11)
        }
        spannableBuilder.bold("pushed to")
            .append(" ")
            .append(ref)
            .append(" ")
            .bold("at")
            .append(" ")
            .append(eventsModel.repo!!.name)
        val commits = eventsModel.payload!!.commits
        val size = commits?.size ?: -1
        val spanCommits = builder()
        if (size > 0) {
            if (size != 1) spanCommits.append(eventsModel.payload!!.size.toString())
                .append(" new commits").append("\n") else spanCommits.append("1 new commit")
                .append("\n")
            val max = 5
            var appended = 0
            for (commit in commits!!) {
                var sha = commit.sha
                if (TextUtils.isEmpty(sha)) continue
                sha = if (sha!!.length > 7) sha.substring(0, 7) else sha
                spanCommits.url(sha).append(" ")
                    .append(
                        if (commit.message != null) commit.message!!.replace(
                            "\\r?\\n|\\r".toRegex(),
                            " "
                        ) else ""
                    )
                    .append("\n")
                appended++
                if (appended == max) break
            }
        }
        if (spanCommits.length > 0) {
            val last = spanCommits.length
            description!!.maxLines = 5
            description.text = spanCommits.delete(last - 1, last)
            description.visibility = View.VISIBLE
        } else {
            description!!.text = ""
            description.maxLines = 2
            description.visibility = View.GONE
        }
    }

    private fun appendPullRequestReviewCommentEvent(
        spannableBuilder: SpannableBuilder,
        eventsModel: Event
    ) {
        val pullRequest = eventsModel.payload!!.pullRequest
        val comment = eventsModel.payload!!.comment
        spannableBuilder.bold("reviewed")
            .append(" ")
            .bold("pull request")
            .append(" ")
            .bold("in")
            .append(" ")
            .append(eventsModel.repo!!.name)
            .bold("#")
            .bold(pullRequest!!.number.toString())
        if (comment!!.body != null) {
            stripMdText(description!!, comment.body!!.replace("\\r?\\n|\\r".toRegex(), " "))
            description.visibility = View.VISIBLE
        } else {
            description!!.text = ""
            description.visibility = View.GONE
        }
    }

    private fun appendPullRequestEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        val issue = eventsModel.payload!!.pullRequest
        var action = eventsModel.payload!!.action
        if ("synchronize" == action) {
            action = "updated"
        }
        if (eventsModel.payload!!.pullRequest!!.merged) {
            action = "merged"
        }
        spannableBuilder.bold(action!!)
            .append(" ")
            .bold("pull request")
            .append(" ")
            .append(eventsModel.repo!!.name)
            .bold("#")
            .bold(issue!!.number.toString())
        if ("opened" == action || "closed" == action) {
            if (issue.title != null) {
                stripMdText(description!!, issue.title!!.replace("\\r?\\n|\\r".toRegex(), " "))
                description.visibility = View.VISIBLE
            } else {
                description!!.text = ""
                description.visibility = View.GONE
            }
        }
    }

    private fun appendPublicEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        var action = "public"
        if (eventsModel.payload != null && "privatized".equals(
                eventsModel.payload!!.action,
                ignoreCase = true
            )
        ) {
            action = "private"
        }
        spannableBuilder.append("made")
            .append(" ")
            .append(eventsModel.repo!!.name)
            .append(" ")
            .append(action)
    }

    private fun appendMemberEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        val user = eventsModel.payload!!.member
        spannableBuilder.bold("added")
            .append(" ")
            .append(if (user != null) user.login + " " else "")
            .append("as a collaborator")
            .append(" ")
            .append("to")
            .append(" ")
            .append(eventsModel.repo!!.name)
    }

    private fun appendIssueEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        val issue = eventsModel.payload!!.issue
        val isLabel = "label" == eventsModel.payload!!.action
        val label =
            if (isLabel) if (issue!!.labels != null && !issue.labels!!.isEmpty()) issue.labels!![issue.labels!!.size - 1] else null else null
        spannableBuilder.bold((if (isLabel && label != null) "Labeled " + label.name else eventsModel.payload!!.action)!!)
            .append(" ")
            .bold("issue")
            .append(" ")
            .append(eventsModel.repo!!.name)
            .bold("#")
            .bold(issue!!.number.toString())
        if (issue.title != null) {
            stripMdText(description!!, issue.title!!.replace("\\r?\\n|\\r".toRegex(), " "))
            description.visibility = View.VISIBLE
        } else {
            description!!.text = ""
            description.visibility = View.GONE
        }
    }

    private fun appendIssueCommentEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        val comment = eventsModel.payload!!.comment
        val issue = eventsModel.payload!!.issue
        spannableBuilder.bold("commented")
            .append(" ")
            .bold("on")
            .append(" ")
            .bold(if (issue!!.pullRequest != null) "pull request" else "issue")
            .append(" ")
            .append(eventsModel.repo!!.name)
            .bold("#")
            .bold(issue.number.toString())
        if (comment!!.body != null) {
            stripMdText(description!!, comment.body!!.replace("\\r?\\n|\\r".toRegex(), " "))
            description.visibility = View.VISIBLE
        } else {
            description!!.text = ""
            description.visibility = View.GONE
        }
    }

    private fun appendGollumEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        val wiki = eventsModel.payload!!.pages
        if (wiki != null && wiki.isNotEmpty()) {
            for (wikiModel in wiki) {
                spannableBuilder.bold(wikiModel.action!!)
                    .append(" ")
                    .append(wikiModel.pageName)
                    .append(" ")
            }
        } else {
            spannableBuilder.bold(resources.getString(R.string.gollum))
                .append(" ")
        }
        spannableBuilder
            .append(eventsModel.repo!!.name)
    }

    private fun appendGistEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        var action = eventsModel.payload!!.action
        action =
            if ("create" == action) "created" else if ("update" == action) "updated" else action
        spannableBuilder.bold(action!!)
            .append(" ")
            .append(itemView.resources.getString(R.string.gist))
            .append(" ")
            .append(eventsModel.payload!!.gist!!.gistId)
    }

    private fun appendForkEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        spannableBuilder.bold("forked")
            .append(" ")
            .append(eventsModel.repo!!.name)
    }

    private fun appendFollowEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        spannableBuilder.bold("started following")
            .append(" ")
            .bold(eventsModel.payload!!.target!!.login!!)
    }

    private fun appendDownloadEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        spannableBuilder.bold("uploaded a file")
            .append(" ")
            .append(if (eventsModel.payload!!.download != null) eventsModel.payload!!.download!!.name else "")
            .append(" ")
            .append("to")
            .append(" ")
            .append(eventsModel.repo!!.name)
    }

    private fun appendCreateEvent(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        val payloadModel = eventsModel.payload
        val refType = payloadModel!!.refType
        spannableBuilder
            .bold("created")
            .append(" ")
            .append(refType)
            .append(" ")
            .append(
                if (!"repository".equals(
                        refType,
                        ignoreCase = true
                    )
                ) payloadModel.ref + " " else ""
            )
            .bold("at")
            .append(" ")
            .append(eventsModel.repo!!.name)
        if (payloadModel.description != null) {
            stripMdText(
                description!!,
                payloadModel.description!!.replace("\\r?\\n|\\r".toRegex(), " ")
            )
            description.visibility = View.VISIBLE
        } else {
            description!!.text = ""
            description.visibility = View.GONE
        }
    }

    private fun appendWatch(
        spannableBuilder: SpannableBuilder,
        type: EventsType,
        eventsModel: Event
    ) {
        spannableBuilder.bold(resources.getString(type.type).lowercase())
            .append(" ")
            .append(eventsModel.repo!!.name)
    }

    private fun appendCommitComment(spannableBuilder: SpannableBuilder, eventsModel: Event) {
        val comment =
            if (eventsModel.payload!!.commitComment == null) eventsModel.payload!!.comment else eventsModel.payload!!
                .commitComment
        val commitId =
            if (comment?.commitId != null && comment.commitId!!.length > 10) comment.commitId!!.substring(
                0,
                10
            ) else null
        spannableBuilder.bold("commented")
            .append(" ")
            .bold("on")
            .append(" ")
            .bold("commit")
            .append(" ")
            .append(eventsModel.repo!!.name)
            .url(if (commitId != null) "@$commitId" else "")
        if (comment?.body != null) {
            stripMdText(description!!, comment.body!!.replace("\\r?\\n|\\r".toRegex(), " "))
            description.visibility = View.VISIBLE
        } else {
            description!!.text = ""
            description.visibility = View.GONE
        }
    }

    private fun appendActor(eventsModel: Event, spannableBuilder: SpannableBuilder) {
        if (eventsModel.actor != null) {
            spannableBuilder.append(eventsModel.actor!!.login).append(" ")
        }
    }

    private fun appendAvatar(eventsModel: Event) {
        if (avatar != null) {
            if (eventsModel.actor != null) {
                avatar.setUrl(
                    eventsModel.actor!!.avatarUrl, eventsModel.actor!!.login,
                    eventsModel.actor!!.isOrganizationType,
                    isEnterprise(eventsModel.actor!!.htmlUrl)
                )
            } else {
                avatar.setUrl(null, null, isOrg = false, isEnterprise = false)
            }
        }
    }

    companion object {
        fun getView(viewGroup: ViewGroup, noImage: Boolean): View {
            return if (noImage) {
                getView(viewGroup, R.layout.feeds_row_no_image_item)
            } else {
                getView(viewGroup, R.layout.feeds_row_item)
            }
        }
    }

}
