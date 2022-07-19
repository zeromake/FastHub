package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.ReactionsModel
import java.lang.reflect.Type

class ReactionsConverter : BaseConverter<ReactionsModel>() {
    override val genericType: Type = genericType<ReactionsModel>()
}