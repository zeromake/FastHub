package com.fastaccess.data.dao.types

import androidx.annotation.DrawableRes
import com.fastaccess.R

/**
 * Created by Kosh on 19 Apr 2017, 7:57 PM
 */
enum class NotificationType(@get:DrawableRes @param:DrawableRes val drawableRes: Int) {
    PullRequest(R.drawable.ic_pull_requests), Issue(R.drawable.ic_issues), Commit(R.drawable.ic_push);
}