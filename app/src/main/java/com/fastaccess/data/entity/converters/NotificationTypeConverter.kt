package com.fastaccess.data.entity.converters

import com.fastaccess.data.entity.FastHubNotification
import io.objectbox.converter.PropertyConverter

class NotificationTypeConverter : PropertyConverter<FastHubNotification.NotificationType?, Int?> {
    override fun convertToEntityProperty(databaseValue: Int?): FastHubNotification.NotificationType? {
        if (databaseValue == null || databaseValue == -1) {
            return null
        }
        return FastHubNotification.NotificationType.values()[databaseValue]
    }

    override fun convertToDatabaseValue(entityProperty: FastHubNotification.NotificationType?): Int {
        return entityProperty?.ordinal ?: -1
    }
}