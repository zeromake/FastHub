package com.fastaccess.data.entity.converters

import com.fastaccess.data.entity.Gist
import java.lang.reflect.Type

class GistConverter : BaseConverter<Gist>() {
    override val genericType: Type = genericType<Gist>()
}