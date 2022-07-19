package com.fastaccess.ui.modules.notification.callback

import com.fastaccess.data.dao.GroupedNotificationModel

interface OnNotificationChangedListener {
    fun onNotificationChanged(notification: GroupedNotificationModel, index: Int)
}