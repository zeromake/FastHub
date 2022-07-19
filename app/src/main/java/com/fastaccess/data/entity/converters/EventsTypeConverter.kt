package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.types.EventsType
import io.objectbox.converter.PropertyConverter

class EventsTypeConverter : PropertyConverter<EventsType?, Int?> {
    override fun convertToEntityProperty(databaseValue: Int?): EventsType? {
        if (databaseValue == null || databaseValue == -1) {
            return null
        }
        return EventsType.values()[databaseValue]
    }

    override fun convertToDatabaseValue(entityProperty: EventsType?): Int {
        return entityProperty?.ordinal ?: -1
    }
}