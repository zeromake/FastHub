package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.ui.adapter.viewholder.SimpleViewHolder
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder

open class SimpleListAdapter<O>(data: MutableList<O>, listener: BaseViewHolder.OnItemClickListener<O>) :
    BaseRecyclerAdapter<O, SimpleViewHolder<O>, BaseViewHolder.OnItemClickListener<O>>(
        data,
        listener
    ) {
    override fun viewHolder(parent: ViewGroup, viewType: Int): SimpleViewHolder<O> {
        return SimpleViewHolder(BaseViewHolder.getView(parent, R.layout.simple_row_item), this)
    }

    override fun onBindView(holder: SimpleViewHolder<O>, position: Int) {
        val item = getItem(position)
        holder.bind(item!!)
    }
}