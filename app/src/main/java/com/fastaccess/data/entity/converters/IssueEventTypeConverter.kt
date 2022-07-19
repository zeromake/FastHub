package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.types.IssueEventType
import io.objectbox.converter.PropertyConverter

class IssueEventTypeConverter : PropertyConverter<IssueEventType?, Int?> {
    override fun convertToEntityProperty(databaseValue: Int?): IssueEventType? {
        if (databaseValue == null || databaseValue == -1) {
            return null
        }
        return IssueEventType.values()[databaseValue]
    }

    override fun convertToDatabaseValue(entityProperty: IssueEventType?): Int {
        return entityProperty?.ordinal ?: -1
    }
}