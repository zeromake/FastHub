package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.LabelModel
import java.lang.reflect.Type

class LabelConverter : BaseConverter<LabelModel>() {
    override val genericType: Type = genericType<LabelModel>()
}