package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.converters.BaseConverter
import com.fastaccess.data.dao.CommitListModel

/**
 * Created by Kosh on 15 Mar 2017, 8:37 PM
 */
class CommitsConverter : BaseConverter<CommitListModel>() {
    override val typeClass: Class<out CommitListModel>
        get() = CommitListModel::class.java
}