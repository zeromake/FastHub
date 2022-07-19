package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.GithubFileModel
import java.lang.reflect.Type

class GitHubFilesConverter : BaseConverter<GithubFileModel>() {
    override val genericType: Type = genericType<GithubFileModel>()
}