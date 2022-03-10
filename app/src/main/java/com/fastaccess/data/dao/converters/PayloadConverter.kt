package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.PayloadModel

/**
 * Created by Kosh on 15 Mar 2017, 8:39 PM
 */
class PayloadConverter : BaseConverter<PayloadModel>() {
    override val typeClass: Class<out PayloadModel>
        get() = PayloadModel::class.java
}