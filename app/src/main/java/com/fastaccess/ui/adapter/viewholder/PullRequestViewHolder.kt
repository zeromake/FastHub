package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.data.entity.PullRequest
import com.fastaccess.data.entity.dao.PullRequestDao
import com.fastaccess.provider.scheme.LinkParserHelper.isEnterprise
import com.fastaccess.ui.adapter.PullRequestAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */
class PullRequestViewHolder private constructor(
    itemView: View,
    adapter: PullRequestAdapter,
    private val withAvatar: Boolean,
    private val showRepoName: Boolean
) : BaseViewHolder<PullRequest>(itemView, adapter) {
    val title: FontTextView = itemView.findViewById(R.id.title)
    val avatarLayout: AvatarLayout? = itemView.findViewById(R.id.avatarLayout)
    val details: FontTextView = itemView.findViewById(R.id.details)
    private val commentsNo: FontTextView = itemView.findViewById(R.id.commentsNo)
    val by: String = itemView.context.resources.getString(R.string.by)
    override fun bind(t: PullRequest) {
        title.text = t.title
        details.text = PullRequestDao.getMergeBy(
            details.context,
            t,
            showRepoName
        )
        if (t.comments > 0) {
            commentsNo.text = t.comments.toString()
            commentsNo.visibility = View.VISIBLE
        } else {
            commentsNo.visibility = View.GONE
        }
        if (withAvatar && avatarLayout != null) {
            avatarLayout.setUrl(
                t.user!!.avatarUrl, t.user!!.login,
                false, isEnterprise(t.htmlUrl)
            )
            avatarLayout.visibility = View.VISIBLE
        }
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(
            viewGroup: ViewGroup?,
            adapter: PullRequestAdapter,
            withAvatar: Boolean,
            showRepoName: Boolean
        ): PullRequestViewHolder {
            return if (withAvatar) {
                PullRequestViewHolder(
                    getView(
                        viewGroup!!,
                        R.layout.issue_row_item
                    ), adapter, true, showRepoName
                )
            } else PullRequestViewHolder(
                getView(
                    viewGroup!!,
                    R.layout.issue_no_image_row_item
                ), adapter, false, showRepoName
            )
        }
    }
}
