package com.fastaccess.data.entity.converters

import com.fastaccess.data.entity.PullRequest
import java.lang.reflect.Type

class PullRequestConverter : BaseConverter<PullRequest>() {
    override val genericType: Type = genericType<PullRequest>()
}