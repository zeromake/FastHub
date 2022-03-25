package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.GitCommitModel

/**
 * Created by Kosh on 15 Mar 2017, 8:42 PM
 */
class GitCommitConverter : BaseConverter<GitCommitModel>() {
    override val typeClass: Class<out GitCommitModel>
        get() = GitCommitModel::class.java
}