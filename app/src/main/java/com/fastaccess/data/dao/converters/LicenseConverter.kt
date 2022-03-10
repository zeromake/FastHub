package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.LicenseModel

/**
 * Created by Kosh on 15 Mar 2017, 8:33 PM
 */
class LicenseConverter : BaseConverter<LicenseModel>() {
    override val typeClass: Class<out LicenseModel>
        get() = LicenseModel::class.java
}