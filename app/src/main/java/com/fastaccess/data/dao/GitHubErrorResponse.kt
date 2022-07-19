package com.fastaccess.data.dao

/**
 * Created by Kosh on 18 Feb 2017, 2:09 PM
 */
class GitHubErrorResponse {
    var message: String? = null
    var documentationUrl: String? = null
    var errors: List<GistHubErrorsModel>? = null
    override fun toString(): String {
        return "GitHubErrorResponse{" +
                "message='" + message + '\'' +
                ", documentation_url='" + documentationUrl + '\'' +
                ", errors=" + errors +
                '}'
    }
}