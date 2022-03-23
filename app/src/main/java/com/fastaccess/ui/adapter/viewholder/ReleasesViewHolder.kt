package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.data.dao.model.Release
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.ParseDateFormat.Companion.getTimeAgo
import com.fastaccess.ui.adapter.ReleasesAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.ForegroundImageView
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */
class ReleasesViewHolder private constructor(
    itemView: View,
    adapter: ReleasesAdapter
) : BaseViewHolder<Release>(itemView, adapter) {
    var title: FontTextView? = itemView.findViewById(R.id.title)
    var details: FontTextView? = itemView.findViewById(R.id.details)
    var download: ForegroundImageView? = itemView.findViewById(R.id.download)
    var released: String = itemView.resources.getString(R.string.released)
    var drafted: String = itemView.resources.getString(R.string.drafted)
    override fun bind(t: Release) {
        title!!.text = builder()
            .bold(if (!isEmpty(t.name)) t.name else t.tagName)
        if (t.author != null) {
            details!!.text = builder()
                .append(t.author.login)
                .append(" ")
                .append(if (t.isDraft) drafted else released)
                .append(" ")
                .append(getTimeAgo(t.createdAt))
        } else {
            details!!.visibility = View.GONE
        }
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(
            viewGroup: ViewGroup?,
            adapter: ReleasesAdapter
        ): ReleasesViewHolder {
            return ReleasesViewHolder(getView(viewGroup!!, R.layout.releases_row_item), adapter)
        }
    }

    init {
        download!!.setOnClickListener(this)
        download!!.setOnLongClickListener(this)
    }
}