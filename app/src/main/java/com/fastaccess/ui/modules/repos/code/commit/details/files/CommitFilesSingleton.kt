package com.fastaccess.ui.modules.repos.code.commit.details.files

import com.fastaccess.data.dao.CommitFileListModel

/**
 * Created by Kosh on 27 Mar 2017, 7:28 PM
 * Commits files could be so freaking large, so having this will avoid transactionToLargeException.
 */
internal class CommitFilesSingleton private constructor() {
    private val files: MutableMap<String, CommitFileListModel> = mutableMapOf()
    fun putFiles(id: String, commitFiles: CommitFileListModel) {
        clear()
        files[id] = commitFiles
    }

    fun getByCommitId(id: String): CommitFileListModel? {
        return files[id]
    }

    fun clear() {
        files.clear()
    }

    companion object {
        val instance = CommitFilesSingleton()
    }
}