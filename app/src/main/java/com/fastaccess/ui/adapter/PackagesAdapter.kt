package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.data.dao.model.GitHubPackage
import com.fastaccess.ui.adapter.viewholder.PackagesViewHolder
import com.fastaccess.ui.adapter.viewholder.PackagesViewHolder.Companion.newInstance
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder

class PackagesAdapter constructor(
    data: MutableList<GitHubPackage>,
) : BaseRecyclerAdapter<GitHubPackage, PackagesViewHolder, BaseViewHolder.OnItemClickListener<GitHubPackage>>(data) {
    override fun viewHolder(parent: ViewGroup, viewType: Int): PackagesViewHolder {
        return newInstance(parent, this)
    }

    override fun onBindView(holder: PackagesViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }
}