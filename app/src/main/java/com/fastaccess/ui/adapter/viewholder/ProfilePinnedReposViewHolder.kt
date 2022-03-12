package com.fastaccess.ui.adapter.viewholder

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.github.GetPinnedReposQuery
import java.text.NumberFormat

/**
 * Created by kosh on 09/08/2017.
 */
class ProfilePinnedReposViewHolder private constructor(
    view: View,
    adapter: BaseRecyclerAdapter<GetPinnedReposQuery.Node, ProfilePinnedReposViewHolder, OnItemClickListener<GetPinnedReposQuery.Node>>
) :
    BaseViewHolder<GetPinnedReposQuery.Node>(view, adapter) {

    val title: FontTextView = view.findViewById(R.id.title)
    val issues: FontTextView = view.findViewById(R.id.issues)
    val pullRequest: FontTextView = view.findViewById(R.id.pullRequests)
    val language: FontTextView = view.findViewById(R.id.language)
    val stars: FontTextView = view.findViewById(R.id.stars)
    val forks: FontTextView = view.findViewById(R.id.forks)

    override fun bind(t: GetPinnedReposQuery.Node) {}

    fun bind(t: GetPinnedReposQuery.Node, numberFormat: NumberFormat) {
        if (t.onRepository != null) {
            val repo = t.onRepository
            title.text = repo.name
            issues.text = numberFormat.format(repo.issues.totalCount)
            pullRequest.text = numberFormat.format(repo.pullRequests.totalCount)
            forks.text = numberFormat.format(repo.forks.totalCount)
            stars.text = numberFormat.format(repo.stargazers.totalCount)
            repo.primaryLanguage?.let {
                language.text = it.name
                it.color?.let { pColor ->
                    if (pColor.startsWith("#")) {
                        language.tintDrawables(Color.parseColor(pColor))
                    } else {
                        val color = "#$pColor"
                        language.tintDrawables(Color.parseColor(color))
                    }
                }
            }
        }

    }

    companion object {
        fun newInstance(
            parent: ViewGroup,
            adapter: BaseRecyclerAdapter<GetPinnedReposQuery.Node, ProfilePinnedReposViewHolder, OnItemClickListener<GetPinnedReposQuery.Node>>
        ): ProfilePinnedReposViewHolder {
            return ProfilePinnedReposViewHolder(
                getView(
                    parent,
                    R.layout.profile_pinned_repo_row_item
                ), adapter
            )
        }
    }
}