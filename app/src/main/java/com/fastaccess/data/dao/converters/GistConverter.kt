package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.converters.BaseConverter
import com.fastaccess.data.dao.model.Gist

/**
 * Created by Kosh on 15 Mar 2017, 8:30 PM
 */
class GistConverter : BaseConverter<Gist>() {
    override val typeClass: Class<out Gist>
        get() = Gist::class.java
}