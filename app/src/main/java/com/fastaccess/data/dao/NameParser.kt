package com.fastaccess.data.dao

import android.net.Uri
import com.fastaccess.helper.InputHelper
import com.fastaccess.provider.scheme.LinkParserHelper

/**
 * Created by Kosh on 11 Feb 2017, 11:03 PM
 */
class NameParser(var url: String?) {
    var name: String? = null
    var username: String? = null
    var isEnterprise = false
    override fun toString(): String {
        return "NameParser{" +
                "name='" + name + '\'' +
                ", username='" + username + '\'' +
                '}'
    }

    init {
        if (!InputHelper.isEmpty(url)) {
            val isEnterprise = LinkParserHelper.isEnterprise(url)
            if (isEnterprise) {
                url = url!!.replace("api/v3/", "")
            }
            val uri = Uri.parse(url)
            val segments = uri.pathSegments
            if (segments?.size!! >= 2) {
                val isFirstPathIsRepo =
                    segments[0].equals("repos", ignoreCase = true) || segments[0].equals(
                        "repo",
                        ignoreCase = true
                    )
                username = if (isFirstPathIsRepo) segments[1] else segments[0]
                name = if (isFirstPathIsRepo) segments[2] else segments[1]
                this.isEnterprise = isEnterprise
            }
        }
    }
}