package com.fastaccess.data.entity.converters

import com.fastaccess.data.entity.Commit
import java.lang.reflect.Type

class CommitConverter : BaseConverter<Commit>() {
    override val genericType: Type = genericType<Commit>()
}
