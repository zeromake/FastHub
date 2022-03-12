package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.ui.adapter.callback.OnToggleView
import com.fastaccess.ui.adapter.viewholder.CommitCommentsViewHolder
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder

/**
 * Created by kosh on 15/08/2017.
 */
class CommitCommentsAdapter(
    data: MutableList<Comment>,
    listener: BaseViewHolder.OnItemClickListener<Comment>,
    var onToggleView: OnToggleView
) : BaseRecyclerAdapter<Comment, CommitCommentsViewHolder,
        BaseViewHolder.OnItemClickListener<Comment>>(data, listener) {

    override fun viewHolder(parent: ViewGroup, viewType: Int): CommitCommentsViewHolder {
        return CommitCommentsViewHolder.newInstance(parent, this, onToggleView)
    }

    override fun onBindView(holder: CommitCommentsViewHolder, position: Int) {
        holder.bind(data[position]!!)
    }
}