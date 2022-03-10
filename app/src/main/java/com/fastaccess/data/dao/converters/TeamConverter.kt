package com.fastaccess.data.dao.converters

import com.fastaccess.data.dao.TeamsModel

/**
 * Created by Kosh on 15 Mar 2017, 7:58 PM
 */
class TeamConverter : BaseConverter<TeamsModel>() {
    override val typeClass: Class<out TeamsModel>
        get() = TeamsModel::class.java
}