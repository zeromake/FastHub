package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.LicenseModel
import java.lang.reflect.Type

class LicenseConverter: BaseConverter<LicenseModel>() {
    override val genericType: Type = genericType<LicenseModel>()
}