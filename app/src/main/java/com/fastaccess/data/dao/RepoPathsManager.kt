package com.fastaccess.data.dao

import com.fastaccess.data.dao.model.RepoFile
import java.util.HashMap

/**
 * Created by Kosh on 03 Mar 2017, 10:43 PM
 */
class RepoPathsManager {
    private val files = HashMap<String, List<RepoFile>>()
    fun getPaths(url: String, ref: String): List<RepoFile>? {
        return files["$ref/$url"]
    }

    fun setFiles(ref: String, path: String, paths: List<RepoFile>) {
        files["$ref/$path"] = paths
    }

    fun clear() {
        files.clear()
    }
}