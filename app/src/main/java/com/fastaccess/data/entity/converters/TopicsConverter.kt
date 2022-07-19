package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.TopicsModel
import java.lang.reflect.Type

class TopicsConverter: BaseConverter<TopicsModel>() {
    override val genericType: Type = genericType<TopicsModel>()
}