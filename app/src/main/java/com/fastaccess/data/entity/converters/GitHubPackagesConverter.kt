package com.fastaccess.data.entity.converters

import com.fastaccess.data.entity.GitHubPackage
import java.lang.reflect.Type

class GitHubPackagesConverter : BaseConverter<GitHubPackage>() {
    override val genericType: Type = genericType<GitHubPackage>()
}