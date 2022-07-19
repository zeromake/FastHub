package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.UsersListModel
import java.lang.reflect.Type

class UsersConverter : BaseConverter<UsersListModel>() {
    override val genericType: Type = genericType<UsersListModel>()
}