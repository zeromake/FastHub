package com.fastaccess.data.entity.converters

import com.fastaccess.data.dao.GithubState
import java.lang.reflect.Type

class GitHubStateConverter:BaseConverter<GithubState>() {
    override val genericType: Type = genericType<GithubState>()
}