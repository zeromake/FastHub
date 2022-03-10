package com.prettifier.pretty.helper

import android.content.Context
import android.net.Uri
import com.fastaccess.data.dao.NameParser
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.PrefGetter.ThemeType
import com.fastaccess.helper.ViewHelper
import org.jsoup.Jsoup
import retrofit2.http.Url
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Kosh on 25 Dec 2016, 9:12 PM
 */
object GithubHelper {
    fun generateContent(
        context: Context, source: String,
        baseUrl: String?, dark: Boolean,
        isWiki: Boolean, replace: Boolean,
        branch: String?,
    ): String {
        return if (baseUrl == null) {
            val content = Jsoup.parse(source).body().html()
            mergeContent(context, content, dark)
        } else {
            mergeContent(context, parseReadme(source, baseUrl, isWiki, branch), dark)
        }
    }

    private fun parseReadme(
        source: String,
        baseUrl: String,
        isWiki: Boolean,
        branch: String?
    ): String {
        val nameParser = NameParser(baseUrl)
        val owner = nameParser.username
        val repoName = nameParser.name
        val uri = Uri.parse(baseUrl)
        val paths = ArrayList(uri.pathSegments)
        val builder = StringBuilder()
        builder.append(owner).append("/").append(repoName).append("/")
        val containsMaster = paths.size > 3
        if (!containsMaster) {
            builder.append("${branch}/")
        } else {
            paths.remove("blob")
        }
        paths.remove(owner)
        paths.remove(repoName)
        for (path in paths) {
            if (!path.equals(uri.lastPathSegment, ignoreCase = true)) {
                builder.append(path).append("/")
            }
        }
        val baseLinkUrl = if (!isWiki) getLinkBaseUrl(baseUrl, branch) else baseUrl
        return getParsedHtml(
            source,
            owner,
            repoName,
            if (!isWiki) builder.toString() else baseUrl,
            baseLinkUrl,
            isWiki,
            branch,
        )
    }

    private fun getParsedHtml(
        source: String, owner: String?, repoName: String?,
        builder: String, baseLinkUrl: String, isWiki: Boolean,
        branch: String? = "master",
    ): String {
        val document = Jsoup.parse(source, "")
        val imageElements = document.getElementsByTag("img")
        if (!imageElements.isEmpty()) {
            for (element in imageElements) {
//                val parent = element.parent()
                val src = element.attr("src")
                if (!(src.startsWith("http://") || src.startsWith("https://"))) {
                    val finalSrc: String = if (src.startsWith("/$owner/$repoName")) {
                        "https://raw.githubusercontent.com/$src"
                    } else {
                        "https://raw.githubusercontent.com/$builder$src"
                    }
                    element.attr("src", finalSrc)
                    element.attr("origin-src", src)
//                    element.attr("href", finalSrc)
                }
            }
        }
        val linkElements = document.getElementsByTag("a")
        if (!linkElements.isEmpty()) {
            for (element in linkElements) {
                val href = element.attr("href")
                if (href.startsWith("#") || href.startsWith("http://") || href.startsWith("https://") || href.startsWith(
                        "mailto:"
                    )
                ) {
                    continue
                }
                val child = element.children().first()
                // a tag child is img, href change to img src
                if (child != null && child.tagName() == "img") {
                    val imgSrc = child.attr("origin-src")
                    if (href.equals(imgSrc)) {
                        element.attr("href", child.attr("src"))
                        element.attr("origin-href", href)
                        continue
                    }
                }
                val finalSrc: String =
                    if (isWiki && href.startsWith("wiki") || href.startsWith("./wiki")) {
                        baseLinkUrl + href.replaceFirst("wiki".toRegex(), "")
                    } else {
                        if (href.startsWith("/$owner/$repoName")) {
                            "https://raw.githubusercontent.com/$href"
                        } else {
                            "https://raw.githubusercontent.com/$builder$href"
                        }
                    }
                element.attr("href", finalSrc)
                element.attr("origin-href", href)
            }
        }
        return document.html()
    }

    private fun getLinkBaseUrl(baseUrl: String, branch: String? = "master"): String {
        val nameParser = NameParser(baseUrl)
        val owner = nameParser.username
        val repoName = nameParser.name
        val uri = Uri.parse(baseUrl)
        val paths = ArrayList(uri.pathSegments)
        val builder = StringBuilder()
        builder.append("https://").append(uri.authority).append("/").append(owner).append("/")
            .append(repoName).append("/")
        val containsMaster = paths.size > 3 && paths[2].equals("blob", ignoreCase = true)
        if (!containsMaster) {
            builder.append("blob/${branch}/")
        }
        paths.remove(owner)
        paths.remove(repoName)
        for (path in paths) {
            if (!path.equals(uri.lastPathSegment, ignoreCase = true)) {
                builder.append(path).append("/")
            }
        }
        return builder.toString()
    }

    private fun mergeContent(context: Context, source: String, dark: Boolean): String {
        // <script src="./intercept-hash.js"></script> <script src="./intercept-touch.js"></script>
        return """${HtmlHelper.HTML_HEADER}
${HtmlHelper.HEAD_HEADER}
    <link rel="stylesheet" type="text/css" href="${getStyle(dark)}">
    ${getCodeStyle(context, dark)}
    <style>
    video {
        width: 100%;
    }
    </style>
${HtmlHelper.HEAD_BOTTOM}
${HtmlHelper.BODY_HEADER}
$source
${HtmlHelper.BODY_BOTTOM}
${HtmlHelper.HTML_BOTTOM}"""
    }

    private fun getStyle(dark: Boolean): String {
        return if (dark) "./github_dark.css" else "./github.css"
    }

    private fun getCodeStyle(context: Context, isDark: Boolean): String {
        if (!isDark) return ""
        val primaryColor = getCodeBackgroundColor(context)
        val accentColor =
            "#" + Integer.toHexString(ViewHelper.getAccentColor(context)).substring(2)
                .uppercase(Locale.getDefault())
        return """<style>
    body .highlight pre, body pre {
        background-color: $primaryColor !important;
        ${if (PrefGetter.getThemeType(context) == PrefGetter.AMLOD) "border: solid 1px $accentColor !important;\n" else ""}
    }
    </style>"""
    }

    private fun getCodeBackgroundColor(context: Context): String {
        @ThemeType val themeType = PrefGetter.themeType
        return if (themeType == PrefGetter.BLUISH) {
            "#" + Integer.toHexString(ViewHelper.getPrimaryDarkColor(context))
                .substring(2).uppercase(Locale.getDefault())
        } else "#" + Integer.toHexString(ViewHelper.getPrimaryColor(context))
            .substring(2).uppercase(Locale.getDefault())
    }
}