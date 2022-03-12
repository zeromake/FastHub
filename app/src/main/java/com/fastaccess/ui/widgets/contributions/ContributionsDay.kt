package com.fastaccess.ui.widgets.contributions

/**
 * Copyright 2016 Javier González
 * All right reserved.
 */
class ContributionsDay(year: Int, month: Int, day: Int, level: Int, data: Int) {
    @JvmField
    var year = Int.MIN_VALUE
    @JvmField
    var month = Int.MIN_VALUE
    @JvmField
    var day = Int.MIN_VALUE

    // Level is used to record the color of the block
    @JvmField
    var level = Int.MIN_VALUE

    // Data is used to calculated the height of the pillar
    @JvmField
    var data = Int.MIN_VALUE

    init {
        this.year = year
        this.month = month
        this.day = day
        this.level = level
        this.data = data
    }
}