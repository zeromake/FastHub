package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.RenameModel

/**
 * Created by Kosh on 15 Mar 2017, 8:29 PM
 */
class RenameConverter : BaseConverter<RenameModel>() {
    override val typeClass: Class<out RenameModel>
        get() = RenameModel::class.java
}