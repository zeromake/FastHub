package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.CommitFileListModel
import java.lang.reflect.Type

class CommitFilesConverter:BaseConverter<CommitFileListModel>() {
    override val genericType: Type = genericType<CommitFileListModel>()
}