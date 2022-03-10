package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.NotificationSubjectModel

/**
 * Created by Kosh on 15 Mar 2017, 7:58 PM
 */
class NotificationSubjectConverter : BaseConverter<NotificationSubjectModel>() {
    override val typeClass: Class<out NotificationSubjectModel>
        get() = NotificationSubjectModel::class.java
}