package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.model.User

/**
 * Created by Kosh on 15 Mar 2017, 7:58 PM
 */
class UserConverter : BaseConverter<User>() {
    override val typeClass: Class<out User>
        get() = User::class.java
}