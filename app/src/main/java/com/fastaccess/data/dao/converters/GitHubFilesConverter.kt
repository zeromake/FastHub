package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.converters.BaseConverter
import com.fastaccess.data.dao.GithubFileModel

/**
 * Created by Kosh on 15 Mar 2017, 8:21 PM
 */
class GitHubFilesConverter : BaseConverter<GithubFileModel>() {
    override val typeClass: Class<out GithubFileModel>
        get() = GithubFileModel::class.java
}