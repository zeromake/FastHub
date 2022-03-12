package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.data.dao.model.Login
import com.fastaccess.ui.adapter.viewholder.LoginViewHolder
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder

/**
 * Created by Kosh on 09 Jul 2017, 5:00 PM
 */
class LoginAdapter constructor(private val small: Boolean = false)
    : BaseRecyclerAdapter<Login, LoginViewHolder, BaseViewHolder.OnItemClickListener<Login>>(null) {

    override fun onBindView(holder: LoginViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    override fun viewHolder(parent: ViewGroup, viewType: Int): LoginViewHolder {
        return LoginViewHolder.newInstance(parent, this, small)
    }
}