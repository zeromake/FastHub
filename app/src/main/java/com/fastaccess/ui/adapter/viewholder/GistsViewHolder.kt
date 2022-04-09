package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.data.entity.Gist
import com.fastaccess.helper.ParseDateFormat.Companion.getTimeAgo
import com.fastaccess.provider.scheme.LinkParserHelper.isEnterprise
import com.fastaccess.ui.adapter.GistsAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */
class GistsViewHolder private constructor(
    itemView: View,
    adapter: GistsAdapter?,
    isFromProfile: Boolean
) : BaseViewHolder<Gist>(itemView, adapter) {
    val avatar: AvatarLayout? = itemView.findViewById(R.id.avatarLayout)
    val title: FontTextView? = itemView.findViewById(R.id.title)
    val date: FontTextView? = itemView.findViewById(R.id.date)
    private val isFromProfile: Boolean
    override fun bind(t: Gist) {
        if (!isFromProfile) {
            if (avatar != null) {
                val url =
                    if (t.owner != null) t.owner!!.avatarUrl else (if (t.user != null) t.user!!.avatarUrl else null)!!
                val login =
                    if (t.owner != null) t.owner!!.login else (if (t.user != null) t.user!!.login else null)!!
                avatar.setUrl(
                    url, login, false, isEnterprise(
                        if (t.owner != null) t.owner!!.htmlUrl else if (t.user != null) t.user!!.htmlUrl else null
                    )
                )
            }
        }
        title!!.text = t.getDisplayTitle(isFromProfile)
        date!!.text = getTimeAgo(t.createdAt)
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(
            viewGroup: ViewGroup,
            adapter: GistsAdapter?,
            isFromProfile: Boolean
        ): GistsViewHolder {
            return if (!isFromProfile) {
                GistsViewHolder(
                    getView(viewGroup, R.layout.feeds_row_item),
                    adapter,
                    false
                )
            } else {
                GistsViewHolder(
                    getView(
                        viewGroup,
                        R.layout.feeds_row_no_image_item
                    ), adapter, true
                )
            }
        }
    }

    init {
        title!!.maxLines = 2
        this.isFromProfile = isFromProfile
    }
}