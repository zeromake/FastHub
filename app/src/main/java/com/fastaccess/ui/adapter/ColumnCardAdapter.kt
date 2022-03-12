package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.data.dao.ProjectCardModel
import com.fastaccess.ui.adapter.viewholder.ColumnCardViewHolder
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder

/**
 * Created by Hashemsergani on 11.09.17.
 */

class ColumnCardAdapter(date: MutableList<ProjectCardModel>, val isOwner: Boolean) :
    BaseRecyclerAdapter<ProjectCardModel, ColumnCardViewHolder, BaseViewHolder.OnItemClickListener<ProjectCardModel>>(
        date,
    ) {
    override fun viewHolder(parent: ViewGroup, viewType: Int): ColumnCardViewHolder =
        ColumnCardViewHolder.newInstance(parent, this, isOwner)

    override fun onBindView(holder: ColumnCardViewHolder, position: Int) {
        holder.bind(data[position]!!)
    }
}