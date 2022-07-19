package com.fastaccess.data.entity.converters

import com.fastaccess.data.entity.Repo
import java.lang.reflect.Type

class RepoConverter : BaseConverter<Repo>() {
    override val genericType: Type = genericType<Repo>()
}