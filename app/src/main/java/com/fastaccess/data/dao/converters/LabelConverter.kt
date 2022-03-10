package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.LabelModel

/**
 * Created by Kosh on 15 Mar 2017, 8:30 PM
 */
class LabelConverter : BaseConverter<LabelModel>() {
    override val typeClass: Class<out LabelModel>
        get() = LabelModel::class.java
}