package com.fastaccess.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.graphics.Color
import androidx.core.content.ContextCompat
import android.text.style.BackgroundColorSpan
import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.data.dao.timeline.PullRequestTimelineModel
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.ViewHelper
import com.fastaccess.provider.scheme.LinkParserHelper
import com.fastaccess.provider.timeline.HtmlHelper
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.ForegroundImageView
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.zzhoujay.markdown.style.CodeSpan
import com.fastaccess.github.PullRequestTimelineQuery
import com.fastaccess.github.type.StatusState

/**
 * Created by kosh on 03/08/2017.
 */

class PullRequestEventViewHolder private constructor(
    view: View,
    adapter: BaseRecyclerAdapter<PullRequestTimelineModel, PullRequestEventViewHolder, OnItemClickListener<PullRequestTimelineModel>>
) :
    BaseViewHolder<PullRequestTimelineModel>(view, adapter) {

    val stateImage: ForegroundImageView = view.findViewById(R.id.stateImage)
    val avatarLayout: AvatarLayout = view.findViewById(R.id.avatarLayout)
    val stateText: FontTextView = view.findViewById(R.id.stateText)
    val commitStatus: ForegroundImageView = view.findViewById(R.id.commitStatus)

    override fun bind(t: PullRequestTimelineModel) {
        val node = t.node
        commitStatus.visibility = View.GONE
        if (node != null) {
            when {
                node.onAssignedEvent != null -> assignedEvent(node.onAssignedEvent)
                node.onBaseRefForcePushedEvent != null -> forcePushEvent(node.onBaseRefForcePushedEvent)
                node.onClosedEvent != null -> closedEvent(node.onClosedEvent)
                node.onCommit != null -> commitEvent(node.onCommit)
                node.onDemilestonedEvent != null -> demilestonedEvent(node.onDemilestonedEvent)
                node.onDeployedEvent != null -> deployedEvent(node.onDeployedEvent)
                node.onHeadRefDeletedEvent != null -> refDeletedEvent(node.onHeadRefDeletedEvent)
                node.onHeadRefForcePushedEvent != null -> refForPushedEvent(node.onHeadRefForcePushedEvent)
                node.onHeadRefRestoredEvent != null -> headRefRestoredEvent(node.onHeadRefRestoredEvent)
                node.onLabeledEvent != null -> labeledEvent(node.onLabeledEvent)
                node.onLockedEvent != null -> lockEvent(node.onLockedEvent)
                node.onMergedEvent != null -> mergedEvent(node.onMergedEvent)
                node.onMilestonedEvent != null -> milestoneEvent(node.onMilestonedEvent)
                node.onReferencedEvent != null -> referenceEvent(node.onReferencedEvent)
                node.onRenamedTitleEvent != null -> renamedEvent(node.onRenamedTitleEvent)
                node.onReopenedEvent != null -> reopenedEvent(node.onReopenedEvent)
                node.onUnassignedEvent != null -> unassignedEvent(node.onUnassignedEvent)
                node.onUnlabeledEvent != null -> unlabeledEvent(node.onUnlabeledEvent)
                node.onUnlockedEvent != null -> unlockedEvent(node.onUnlockedEvent)
                else -> reset()
            }
        } else {
            reset()
        }
    }

    private fun reset() {
        stateText.text = ""
        avatarLayout.setUrl(null, null, false, false)
    }

    @SuppressLint("SetTextI18n")
    private fun unlockedEvent(event: PullRequestTimelineQuery.OnUnlockedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("unlocked this conversation")
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_lock)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun unlabeledEvent(event: PullRequestTimelineQuery.OnUnlabeledEvent) {
        event.actor?.let {
            val color = Color.parseColor("#" + event.label.color)
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("removed")
                .append(" ")
                .append(
                    event.label.name,
                    CodeSpan(color, ViewHelper.generateTextColor(color), 5.0f)
                )
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_label)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun unassignedEvent(event: PullRequestTimelineQuery.OnUnassignedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("unassigned") //TODO add "removed their assignment" for self
                .append(" ")
                .append(event.user?.login)
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_profile)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun reopenedEvent(event: PullRequestTimelineQuery.OnReopenedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("reopened this")
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_issue_opened)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun renamedEvent(event: PullRequestTimelineQuery.OnRenamedTitleEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("changed the title from").append(" ").append(event.previousTitle)
                .append(" ").append("to").append(" ").bold(event.currentTitle)
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_edit)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun referenceEvent(event: PullRequestTimelineQuery.OnReferencedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("referenced in")
                .append(" ")
                .append("from").append(" ")
                .url(
                    if (event.commit != null) {
                        substring(event.commit.oid.toString())
                    } else if (event.subject.onIssue != null) {
                        if (event.isCrossRepository) {
                            "${event.commitRepository.nameWithOwner} ${event.subject.onIssue.title}#${event.subject.onIssue.number}"
                        } else {
                            "${event.subject.onIssue.title}#${event.subject.onIssue.number}"
                        }
                    } else if (event.subject.onPullRequest != null) {
                        if (event.isCrossRepository) {
                            "${event.commitRepository.nameWithOwner} ${event.subject.onPullRequest.title}" +
                                    "#${event.subject.onPullRequest.number}"
                        } else {
                            "${event.subject.onPullRequest.title}#${event.subject.onPullRequest.number}"
                        }
                    } else {
                        event.commitRepository.nameWithOwner
                    }
                )
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_push)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun milestoneEvent(event: PullRequestTimelineQuery.OnMilestonedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("added this to the")
                .append(" ")
                .append(event.milestoneTitle).append(" ").append("milestone")
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_milestone)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun mergedEvent(event: PullRequestTimelineQuery.OnMergedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("merged commit")
                .append(" ")
                .url(substring(event.commit?.oid?.toString()))
                .append(" ")
                .append("into")
                .append(" ")
                .append(event.actor.toString())
                .append(":")
                .append(
                    event.mergeRefName,
                    BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.themeType))
                )
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_merge)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun lockEvent(event: PullRequestTimelineQuery.OnLockedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("locked and limited conversation to collaborators")
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_lock)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun labeledEvent(event: PullRequestTimelineQuery.OnLabeledEvent) {
        event.actor?.let {
            val color = Color.parseColor("#" + event.label.color)
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("labeled")
                .append(" ")
                .append(
                    event.label.name,
                    CodeSpan(color, ViewHelper.generateTextColor(color), 5.0f)
                )
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_label)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun headRefRestoredEvent(event: PullRequestTimelineQuery.OnHeadRefRestoredEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("restored the")
                .append(" ")
                .append(it.login)
                .append(":")
                .append(
                    event.pullRequest.headRefName,
                    BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.themeType))
                )
                .append(" ")
                .append("branch")
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_push)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun refForPushedEvent(event: PullRequestTimelineQuery.OnHeadRefForcePushedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append(
                    "reference force pushed to",
                    BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.themeType))
                )
                .append(" ")
                .url(substring(event.afterCommit?.oid.toString()))
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_push)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun refDeletedEvent(event: PullRequestTimelineQuery.OnHeadRefDeletedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("deleted the")
                .append(" ")
                .append(it.login)
                .append(":")
                .append(
                    substring(event.headRefName),
                    BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.themeType))
                )
                .append(" ")
                .append("branch")
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_trash)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun deployedEvent(event: PullRequestTimelineQuery.OnDeployedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append(
                    "made a deployment",
                    BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.themeType))
                )
                .append(" ")
                .append(event.deployment.latestStatus?.state?.rawValue)
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_push)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun demilestonedEvent(event: PullRequestTimelineQuery.OnDemilestonedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("removed this from the")
                .append(" ")
                .append(event.milestoneTitle).append(" ").append("milestone")
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_milestone)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun commitEvent(event: PullRequestTimelineQuery.OnCommit) {
        event.author?.let {
            stateText.text =
                SpannableBuilder.builder()//Review[k0shk0sh] We may want to suppress more then 3 or 4 commits. since it will clog the it
                    .bold(if (it.user == null) it.name!! else it.user.login)
                    .append(" ")
                    .append("committed")
                    .append(" ")
                    .append(event.messageHeadline)
                    .append(" ")
                    .url(substring(event.oid.toString()))
                    .append(" ")
                    .append(ParseDateFormat.getTimeAgo((event.committedDate.toString())))
            stateImage.setImageResource(R.drawable.ic_push)
            avatarLayout.setUrl(
                it.user?.avatarUrl.toString(), it.user?.login, false,
                LinkParserHelper.isEnterprise(it.user?.url.toString())
            )
            event.status?.let { status1 ->
                commitStatus.visibility = View.VISIBLE
                val context = commitStatus.context
                commitStatus.tintDrawableColor(
                    when (status1.state) {
                        StatusState.ERROR -> ContextCompat.getColor(
                            context,
                            R.color.material_red_700
                        )
                        StatusState.FAILURE -> ContextCompat.getColor(
                            context,
                            R.color.material_deep_orange_700
                        )
                        StatusState.SUCCESS -> ContextCompat.getColor(
                            context,
                            R.color.material_green_700
                        )
                        else -> ContextCompat.getColor(context, R.color.material_yellow_700)
                    }
                )
            }
        }
    }

    private fun closedEvent(event: PullRequestTimelineQuery.OnClosedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("closed this in")
                .append(" ")
                .url(substring(event.id))
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_merge)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun forcePushEvent(event: PullRequestTimelineQuery.OnBaseRefForcePushedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append(
                    "force pushed to",
                    BackgroundColorSpan(HtmlHelper.getWindowBackground(PrefGetter.themeType))
                )
                .append(" ")
                .url(substring(event.afterCommit?.oid.toString()))
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_push)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun assignedEvent(event: PullRequestTimelineQuery.OnAssignedEvent) {
        event.actor?.let {
            stateText.text = SpannableBuilder.builder()
                .bold(it.login)
                .append(" ")
                .append("assigned")
                .append(" ")
                .append(event.assignee?.onUser?.login)// TODO add "self-assigned" for self
                .append(" ")
                .append(ParseDateFormat.getTimeAgo((event.createdAt.toString())))
            stateImage.setImageResource(R.drawable.ic_profile)
            avatarLayout.setUrl(
                it.avatarUrl.toString(),
                it.login,
                false,
                LinkParserHelper.isEnterprise(it.url.toString())
            )
        }
    }

    private fun substring(value: String?): String {
        if (value == null) {
            return ""
        }
        return if (value.length <= 7) value
        else value.substring(0, 7)
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            adapter: BaseRecyclerAdapter<PullRequestTimelineModel, PullRequestEventViewHolder, OnItemClickListener<PullRequestTimelineModel>>
        ): PullRequestEventViewHolder {
            return PullRequestEventViewHolder(
                getView(parent, R.layout.issue_timeline_row_item),
                adapter
            )
        }
    }
}