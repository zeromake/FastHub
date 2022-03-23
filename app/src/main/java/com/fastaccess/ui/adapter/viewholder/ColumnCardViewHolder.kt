package com.fastaccess.ui.adapter.viewholder

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.fastaccess.R
import com.fastaccess.data.dao.ProjectCardModel
import com.fastaccess.data.dao.PullsIssuesParser
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.ui.adapter.ColumnCardAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder

/**
 * Created by Hashemsergani on 11.09.17.
 */
class ColumnCardViewHolder private constructor(
    item: View,
    adapter: ColumnCardAdapter,
    val isOwner: Boolean
) : BaseViewHolder<ProjectCardModel>(item, adapter) {
    val title: TextView = item.findViewById(R.id.title)
    val addedBy: TextView = item.findViewById(R.id.addedBy)
    val editCard: View = item.findViewById(R.id.editCard)

    init {
        editCard.setOnClickListener(this)
    }

    @SuppressLint("StringFormatMatches")
    override fun bind(t: ProjectCardModel) {
        title.text = if (t.note.isNullOrBlank()) {
            val issue = PullsIssuesParser.getForIssue(t.contentUrl!!)
            if (issue != null) {
                "${issue.login}/${issue.repoId}/${issue.number}"
            } else {
                val pr = PullsIssuesParser.getForPullRequest(t.contentUrl!!)
                if (pr != null) {
                    "${pr.login}/${pr.repoId}/${pr.number}"
                } else {
                    "(FastHub) - to be fixed by GitHub! Sorry!"
                }
            }
        } else {
            t.note
        }
        addedBy.text = itemView.context.getString(
            R.string.card_added_by,
            t.creator?.login,
            ParseDateFormat.getTimeAgo(t.createdAt)
        )
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            adapter: ColumnCardAdapter,
            isOwner: Boolean
        ): ColumnCardViewHolder {
            return ColumnCardViewHolder(
                getView(parent, R.layout.column_card_row_layout),
                adapter,
                isOwner
            )
        }
    }
}