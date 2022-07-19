package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.types.IssueState
import io.objectbox.converter.PropertyConverter

class IssueStateConverter : PropertyConverter<IssueState?, Int?> {
    override fun convertToEntityProperty(databaseValue: Int?): IssueState? {
        if (databaseValue == null || databaseValue == -1) {
            return null
        }
        return IssueState.values()[databaseValue]
    }

    override fun convertToDatabaseValue(entityProperty: IssueState?): Int {
        return entityProperty?.ordinal ?: -1
    }
}