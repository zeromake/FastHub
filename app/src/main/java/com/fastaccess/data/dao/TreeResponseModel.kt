package com.fastaccess.data.dao

import com.fastaccess.data.dao.model.RepoFile

/**
 * Created by Kosh on 12 Apr 2017, 1:12 PM
 */
class TreeResponseModel {
    var sha: String? = null
    var url: String? = null
    var truncated = false
    var tree: List<RepoFile>? = null
}