package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.types.FilesType
import io.objectbox.converter.PropertyConverter

class FilesTypeConverter : PropertyConverter<FilesType?, Int?> {
    override fun convertToEntityProperty(databaseValue: Int?): FilesType? {
        if (databaseValue == null || databaseValue == -1) {
            return null
        }
        return FilesType.values()[databaseValue]
    }

    override fun convertToDatabaseValue(entityProperty: FilesType?): Int {
        return entityProperty?.ordinal ?: -1
    }
}