package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.model.GitHubPackage

class GitHubPackagesConverter : BaseConverter<GitHubPackage>() {
    override val typeClass: Class<out GitHubPackage>
        get() = GitHubPackage::class.java
}