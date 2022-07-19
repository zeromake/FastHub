package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.NotificationSubjectModel
import java.lang.reflect.Type

class NotificationSubjectConverter:BaseConverter<NotificationSubjectModel>() {
    override val genericType: Type = genericType<NotificationSubjectModel>()
}