package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.converters.BaseConverter
import com.fastaccess.data.dao.CommitFileListModel

/**
 * Created by Kosh on 15 Mar 2017, 8:37 PM
 */
class CommitFilesConverter : BaseConverter<CommitFileListModel>() {
    override val typeClass: Class<out CommitFileListModel>
        get() = CommitFileListModel::class.java
}