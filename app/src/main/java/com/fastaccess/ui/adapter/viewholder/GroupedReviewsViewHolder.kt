package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.fastaccess.R
import com.fastaccess.data.dao.ReviewCommentModel
import com.fastaccess.data.dao.TimelineModel
import com.fastaccess.helper.ViewHelper.getPatchAdditionColor
import com.fastaccess.helper.ViewHelper.getPatchDeletionColor
import com.fastaccess.helper.ViewHelper.getPatchRefColor
import com.fastaccess.ui.adapter.IssuesTimelineAdapter
import com.fastaccess.ui.adapter.ReviewCommentsAdapter
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.adapter.callback.ReactionsCallback
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.timeline.timeline.PullRequestTimelineMvp.ReviewCommentCallback
import com.fastaccess.ui.widgets.DiffLineSpan.Companion.getSpannable
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.ForegroundImageView
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView

/**
 * Created by Kosh on 13 Dec 2016, 1:42 AM
 */
class GroupedReviewsViewHolder private constructor(
    itemView: View,
    private val viewGroup: ViewGroup,
    adapter: IssuesTimelineAdapter,
    private val onToggleView: OnToggleView,
    private val reactionsCallback: ReactionsCallback,
    reviewCommentCallback: ReviewCommentCallback,
    repoOwner: String, poster: String
) : BaseViewHolder<TimelineModel>(itemView, adapter),
    BaseViewHolder.OnItemClickListener<ReviewCommentModel> {
    val stateImage: ForegroundImageView? = itemView.findViewById(R.id.stateImage)
    val nestedRecyclerView: DynamicRecyclerView? = itemView.findViewById(R.id.nestedRecyclerView)
    val name: FontTextView? = itemView.findViewById(R.id.name)
    val toggle: ForegroundImageView? = itemView.findViewById(R.id.toggle)
    val patch: FontTextView? = itemView.findViewById(R.id.patch)
    val minimized: View? = itemView.findViewById(R.id.minimized)
    val addCommentPreview: View? = itemView.findViewById(R.id.addCommentPreview)
    val toggleHolder: LinearLayout? = itemView.findViewById(R.id.toggleHolder)
    val bottomToggle: View? = itemView.findViewById(R.id.bottomToggle)
    private val patchAdditionColor: Int
    private val patchDeletionColor: Int
    private val patchRefColor: Int
    private var pathText: String? = null
    private val reviewCommentCallback: ReviewCommentCallback?
    private val repoOwner: String
    private val poster: String
    override fun onClick(v: View) {
        if (v.id == R.id.toggle || v.id == R.id.toggleHolder || v.id == R.id.bottomToggle) {
            val position = id
            onToggleView.onToggle(position, !onToggleView.isCollapsed(position))
            onToggle(onToggleView.isCollapsed(position), true)
        } else {
            super.onClick(v)
        }
    }

    override fun bind(t: TimelineModel) {
        val groupedReviewModel = t.groupedReviewModel
        pathText = groupedReviewModel!!.diffText
        name!!.text = groupedReviewModel.path
        stateImage!!.setImageResource(R.drawable.ic_eye)
        if (groupedReviewModel.comments == null || groupedReviewModel.comments!!.isEmpty()) {
            nestedRecyclerView!!.visibility = View.GONE
            nestedRecyclerView.adapter = null
        } else {
            nestedRecyclerView!!.visibility = View.VISIBLE
            nestedRecyclerView.adapter = ReviewCommentsAdapter(
                groupedReviewModel.comments!!, this,
                onToggleView, reactionsCallback, repoOwner, poster
            )
            nestedRecyclerView.addDivider()
        }
        onToggle(onToggleView.isCollapsed(id), false)
    }

    override fun onItemClick(position: Int, v: View?, item: ReviewCommentModel) {
        reviewCommentCallback?.onClick(absoluteAdapterPosition, position, v!!, item)
    }

    override fun onItemLongClick(position: Int, v: View?, item: ReviewCommentModel) {
        reviewCommentCallback?.onLongClick(absoluteAdapterPosition, position, v!!, item)
    }

    private fun onToggle(expanded: Boolean, animate: Boolean) {
        if (!expanded) {
            minimized!!.visibility = View.GONE
            patch!!.text = ""
            name!!.maxLines = 2
            toggle!!.rotation = 0.0f
        } else {
            minimized!!.visibility = View.VISIBLE
            name!!.maxLines = 5
            setPatchText(pathText!!)
            toggle!!.rotation = 180f
        }
    }

    private val id: Long
        get() = absoluteAdapterPosition.toLong()

    private fun setPatchText(text: String) {
        patch!!.text =
            getSpannable(text, patchAdditionColor, patchDeletionColor, patchRefColor, true)
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(
            viewGroup: ViewGroup,
            adapter: IssuesTimelineAdapter,
            onToggleView: OnToggleView,
            reactionsCallback: ReactionsCallback,
            reviewCommentCallback: ReviewCommentCallback,
            repoOwner: String,
            poster: String
        ): GroupedReviewsViewHolder {
            return GroupedReviewsViewHolder(
                getView(viewGroup, R.layout.grouped_review_timeline_row_item), viewGroup, adapter,
                onToggleView, reactionsCallback, reviewCommentCallback, repoOwner, poster
            )
        }
    }

    init {
        this.reviewCommentCallback = reviewCommentCallback
        patchAdditionColor = getPatchAdditionColor(itemView.context)
        patchDeletionColor = getPatchDeletionColor(itemView.context)
        patchRefColor = getPatchRefColor(itemView.context)
        this.repoOwner = repoOwner
        this.poster = poster
        bottomToggle!!.setOnClickListener(this)
        nestedRecyclerView!!.isNestedScrollingEnabled = false
        addCommentPreview!!.setOnClickListener(this)
        toggle!!.setOnClickListener(this)
        toggleHolder!!.setOnClickListener(this)
        itemView.setOnClickListener(null)
        itemView.setOnLongClickListener(null)
    }
}