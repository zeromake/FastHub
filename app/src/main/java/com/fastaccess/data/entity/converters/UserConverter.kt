package com.fastaccess.data.entity.converters

import com.fastaccess.data.entity.User
import java.lang.reflect.Type

class UserConverter : BaseConverter<User>() {
    override val genericType: Type = genericType<User>()
}
