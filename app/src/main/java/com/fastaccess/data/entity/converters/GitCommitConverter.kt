package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.GitCommitModel
import java.lang.reflect.Type

class GitCommitConverter : BaseConverter<GitCommitModel>() {
    override val genericType: Type = genericType<GitCommitModel>()
}