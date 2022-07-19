package com.fastaccess.ui.adapter.viewholder

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.fastaccess.R
import com.fastaccess.data.entity.User
import com.fastaccess.helper.ViewHelper.getWindowBackground
import com.fastaccess.provider.scheme.LinkParserHelper.isEnterprise
import com.fastaccess.ui.adapter.AssigneesAdapter
import com.fastaccess.ui.adapter.AssigneesAdapter.OnSelectAssignee
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView

/**
 * Created by Kosh on 11 Nov 2016, 2:08 PM
 */
class AssigneesViewHolder private constructor(
    itemView: View, private val onSelectAssignee: OnSelectAssignee?,
    adapter: AssigneesAdapter?
) : BaseViewHolder<User>(itemView, adapter) {
    var avatar: AvatarLayout? = itemView.findViewById(R.id.avatarLayout)
    var title: FontTextView? = itemView.findViewById(R.id.title)
    var date: FontTextView? = itemView.findViewById(R.id.date)
    var lightGray = ContextCompat.getColor(itemView.context, R.color.light_gray)
    override fun onClick(v: View) {
        if (onSelectAssignee != null) {
            val position = absoluteAdapterPosition
            onSelectAssignee.onToggleSelection(
                position,
                !onSelectAssignee.isAssigneeSelected(position)
            )
        } else {
            super.onClick(v)
        }
    }

    override fun bind(t: User) {
        avatar!!.setUrl(
            t.avatarUrl,
            t.login,
            t.isOrganizationType,
            isEnterprise(t.htmlUrl)
        )
        title!!.text = t.login
        date!!.visibility = View.GONE
        if (onSelectAssignee != null) {
            itemView.setBackgroundColor(
                if (onSelectAssignee.isAssigneeSelected(absoluteAdapterPosition)) lightGray else getWindowBackground(
                    itemView.context
                )
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(
            viewGroup: ViewGroup, onSelectAssignee: OnSelectAssignee?,
            adapter: AssigneesAdapter?
        ): AssigneesViewHolder {
            return AssigneesViewHolder(
                getView(viewGroup, R.layout.feeds_row_item),
                onSelectAssignee,
                adapter
            )
        }
    }
}