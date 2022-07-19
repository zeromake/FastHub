package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.PayloadModel
import java.lang.reflect.Type

class PayloadConverter:BaseConverter<PayloadModel>() {
    override val genericType: Type = genericType<PayloadModel>()
}