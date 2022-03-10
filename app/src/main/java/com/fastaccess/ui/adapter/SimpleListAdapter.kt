package com.fastaccess.ui.adapter

import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter
import com.fastaccess.ui.adapter.viewholder.SimpleViewHolder
import android.view.ViewGroup
import com.fastaccess.R

open class SimpleListAdapter<O>(data: List<O>, listener: BaseViewHolder.OnItemClickListener<O>) :
    BaseRecyclerAdapter<O, SimpleViewHolder<O>, BaseViewHolder.OnItemClickListener<O>>(
        data,
        listener
    ) {
    override fun viewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder<O> {
        return SimpleViewHolder(BaseViewHolder.getView(parent, R.layout.simple_row_item), this)
    }

    override fun onBindView(holder: SimpleViewHolder<O>, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}