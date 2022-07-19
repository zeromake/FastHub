package com.fastaccess.ui.adapter.viewholder


import android.view.View
import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.data.entity.GitHubPackage
import com.fastaccess.helper.ParseDateFormat.Companion.getTimeAgo
import com.fastaccess.ui.adapter.PackagesAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.FontTextView


class PackagesViewHolder private constructor(
    itemView: View,
    adapter: PackagesAdapter
) : BaseViewHolder<GitHubPackage>(itemView, adapter) {
    var title: FontTextView = itemView.findViewById(R.id.title)
    var date: FontTextView = itemView.findViewById(R.id.date)
    var pType: FontTextView = itemView.findViewById(R.id.pType)

    override fun bind(t: GitHubPackage) {
        title.text = t.name
        date.text = getTimeAgo(t.updated_at)
        pType.text = t.package_type
    }

    companion object {
        @kotlin.jvm.JvmStatic
        fun newInstance(
            viewGroup: ViewGroup?,
            adapter: PackagesAdapter
        ): PackagesViewHolder {
            return PackagesViewHolder(
                    getView(
                        viewGroup!!,
                        R.layout.packages_row_item
                    ), adapter
                )
            }
    }

}