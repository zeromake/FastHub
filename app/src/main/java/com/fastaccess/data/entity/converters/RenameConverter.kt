package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.RenameModel
import java.lang.reflect.Type

class RenameConverter : BaseConverter<RenameModel>() {
    override val genericType: Type = genericType<RenameModel>()
}