package com.fastaccess.ui.adapter

import kotlin.jvm.JvmOverloads
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.adapter.viewholder.FeedsViewHolder
import com.fastaccess.ui.base.adapter.BaseViewHolder
import android.view.ViewGroup
import com.fastaccess.data.entity.Event

/**
 * Created by Kosh on 11 Nov 2016, 2:07 PM
 */
class FeedsAdapter @JvmOverloads constructor(
    events: MutableList<Event>,
    private val noImage: Boolean = false
) : BaseRecyclerAdapter<Event, FeedsViewHolder, BaseViewHolder.OnItemClickListener<Event>>(
    events
) {
    override fun viewHolder(parent: ViewGroup, viewType: Int): FeedsViewHolder {
        return FeedsViewHolder(FeedsViewHolder.getView(parent, noImage), this)
    }

    override fun onBindView(holder: FeedsViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}