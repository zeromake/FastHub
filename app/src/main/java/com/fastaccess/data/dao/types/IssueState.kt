package com.fastaccess.data.dao.types

import androidx.annotation.StringRes
import com.fastaccess.R

enum class IssueState(
    @get:StringRes
    @param:StringRes var status: Int
) {
    `open`(R.string.opened), closed(R.string.closed), all(R.string.all)

}