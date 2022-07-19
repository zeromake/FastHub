package com.fastaccess.provider.rest.interceptors

import android.net.Uri
import com.fastaccess.helper.InputHelper.isEmpty
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

class PaginationInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        val response: Response = chain.proceed(request)
        val headers: Headers = chain.request().headers
        if (headers.values("Accept").contains("application/vnd.github.html") ||
            headers.values("Accept").contains("application/vnd.github.VERSION.raw")
        ) {
            return response //return them as they are.
        }
        if (response.isSuccessful) {
            if (response.peekBody(1).string() == "[") {
                var json = "{"
                val link = response.header("link")
                if (link != null) {
                    val links = link.split(",").toTypedArray()
                    for (link1 in links) {
                        val pageLink = link1.split(";").toTypedArray()
                        val page = Uri.parse(pageLink[0].replace("[<>]".toRegex(), ""))
                            .getQueryParameter("page")
                        val rel = pageLink[1].replace("\"".toRegex(), "").replace("rel=", "")
                        if (page != null) json += String.format(
                            "\"%s\":\"%s\",",
                            rel.trim { it <= ' ' },
                            page
                        )
                    }
                }
                json += String.format("\"items\":%s}", response.body!!.string())
                return response.newBuilder()
                    .body(json.toResponseBody(
                        response.body!!.contentType()
                    )).build()
            } else if (response.header("link") != null) {
                val link = response.header("link")
                var pagination = ""
                val links = link!!.split(",").toTypedArray()
                for (link1 in links) {
                    val pageLink = link1.split(";").toTypedArray()
                    val page = Uri.parse(pageLink[0].replace("[<>]".toRegex(), ""))
                        .getQueryParameter("page")
                    val rel = pageLink[1].replace("\"".toRegex(), "").replace("rel=", "")
                    if (page != null) pagination += String.format(
                        "\"%s\":\"%s\",",
                        rel.trim { it <= ' ' },
                        page
                    )
                }
                if (!isEmpty(pagination)) { //hacking for search pagination.
                    val body = response.body!!.string()
                    return response.newBuilder().body(
                        ("{" + pagination + body.substring(1, body.length)).toResponseBody(
                            response.body!!.contentType()
                        )
                    ).build()
                }
            }
        }
        return response
    }
}