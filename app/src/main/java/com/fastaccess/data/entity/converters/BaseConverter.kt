package com.fastaccess.data.entity.converters

import com.fastaccess.provider.rest.RestProvider
import com.google.gson.reflect.TypeToken
import io.objectbox.converter.PropertyConverter
import java.lang.reflect.Type

abstract class BaseConverter<T> : PropertyConverter<T?, String?> {
    abstract val genericType: Type
    override fun convertToEntityProperty(databaseValue: String?): T? {
        databaseValue ?: return null
        return RestProvider.gson.fromJson<T>(databaseValue, genericType)
    }

    override fun convertToDatabaseValue(entityProperty: T?): String? {
        entityProperty ?: return null
        return RestProvider.gson.toJson(entityProperty)
    }
    companion object {
        inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type
    }
}