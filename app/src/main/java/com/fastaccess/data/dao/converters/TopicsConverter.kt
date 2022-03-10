package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.TopicsModel

/**
 * Created by Kosh on 09 May 2017, 7:54 PM
 */
class TopicsConverter : BaseConverter<TopicsModel>() {
    override val typeClass: Class<out TopicsModel>
        get() = TopicsModel::class.java
}