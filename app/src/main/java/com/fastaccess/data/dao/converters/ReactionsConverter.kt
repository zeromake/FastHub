package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.ReactionsModel

/**
 * Created by Kosh on 06 May 2017, 4:53 PM
 */
class ReactionsConverter : BaseConverter<ReactionsModel>() {
    override val typeClass: Class<out ReactionsModel>
        get() = ReactionsModel::class.java
}