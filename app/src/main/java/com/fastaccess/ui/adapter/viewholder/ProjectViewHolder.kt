package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.github.RepoProjectsOpenQuery
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.ui.adapter.ProjectsAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.FontTextView

/**
 * Created by kosh on 09/09/2017.
 */
class ProjectViewHolder(
    view: View,
    adapter: ProjectsAdapter
) : BaseViewHolder<RepoProjectsOpenQuery.Node>(view, adapter) {
    val description: FontTextView = view.findViewById(R.id.description)
    val title: FontTextView = view.findViewById(R.id.title)
    val date: FontTextView = view.findViewById(R.id.date)

    override fun bind(t: RepoProjectsOpenQuery.Node) {
        title.text = t.name
        if (t.body.isNullOrBlank()) {
            description.visibility = View.GONE
        } else {
            description.visibility = View.VISIBLE
            description.text = t.body
        }
        date.text = ParseDateFormat.getTimeAgo(t.createdAt.toString())
    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            adapter: ProjectsAdapter
        ): ProjectViewHolder {
            return ProjectViewHolder(getView(parent, R.layout.feeds_row_no_image_item), adapter)
        }
    }
}