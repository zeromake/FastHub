package com.fastaccess.data.dao

import androidx.annotation.IntDef

/**
 * Created by JediB on 5/12/2017.
 */
class SettingsModel {
    @IntDef(THEME, NOTIFICATION, CUSTOMIZATION, BEHAVIOR, BACKUP, LANGUAGE, CODE_THEME)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class SettingsType

    var image = 0
    var title: String? = null

    @SettingsType
    var settingsType = 0

    constructor() {}
    constructor(image: Int, title: String?, settingsType: Int) {
        this.image = image
        this.title = title
        this.settingsType = settingsType
    }

    companion object {
        const val THEME = 0
        const val NOTIFICATION = 1
        const val BEHAVIOR = 2
        const val CUSTOMIZATION = 3
        const val BACKUP = 4
        const val LANGUAGE = 5
        const val CODE_THEME = 6
    }
}