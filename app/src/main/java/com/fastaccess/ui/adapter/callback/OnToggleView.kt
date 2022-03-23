package com.fastaccess.ui.adapter.callback

interface OnToggleView {
    fun onToggle(id: Long, isCollapsed: Boolean): Boolean
    fun isCollapsed(id: Long): Boolean
}