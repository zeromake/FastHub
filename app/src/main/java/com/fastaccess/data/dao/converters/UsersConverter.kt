package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.UsersListModel

/**
 * Created by Kosh on 15 Mar 2017, 8:26 PM
 */
class UsersConverter : BaseConverter<UsersListModel>() {
    override val typeClass: Class<out UsersListModel>
        get() = UsersListModel::class.java
}