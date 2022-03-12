package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.ui.adapter.viewholder.ProfilePinnedReposViewHolder
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.github.GetPinnedReposQuery
import java.text.NumberFormat

/**
 * Created by kosh on 09/08/2017.
 */

class ProfilePinnedReposAdapter(data: MutableList<GetPinnedReposQuery.Node>) : BaseRecyclerAdapter<GetPinnedReposQuery.Node,
        ProfilePinnedReposViewHolder, BaseViewHolder.OnItemClickListener<GetPinnedReposQuery.Node>>(data) {

    private val numberFormat = NumberFormat.getNumberInstance()

    override fun viewHolder(parent: ViewGroup, viewType: Int): ProfilePinnedReposViewHolder {
        return ProfilePinnedReposViewHolder.newInstance(parent, this)
    }

    override fun onBindView(holder: ProfilePinnedReposViewHolder, position: Int) {
        holder.bind(data[position]!!, numberFormat)
    }

}