package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.TeamsModel
import java.lang.reflect.Type

class TeamConverter:BaseConverter<TeamsModel>() {
    override val genericType: Type = genericType<TeamsModel>()
}