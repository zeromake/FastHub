package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.CommitListModel
import java.lang.reflect.Type

class CommitsConverter:BaseConverter<CommitListModel>() {
    override val genericType: Type = genericType<CommitListModel>()
}