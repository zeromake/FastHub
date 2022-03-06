package com.prettifier.pretty.helper

import android.content.Context
import android.net.Uri
import com.fastaccess.data.dao.NameParser
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.PrefGetter.ThemeType
import com.fastaccess.helper.ViewHelper
import org.jsoup.Jsoup
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Kosh on 25 Dec 2016, 9:12 PM
 */
object GithubHelper {
    fun generateContent(
        context: Context, source: String,
        baseUrl: String?, dark: Boolean,
        isWiki: Boolean, replace: Boolean
    ): String {
        return if (baseUrl == null) {
            val content = Jsoup.parse(source).body().html()
            mergeContent(context, content, dark)
        } else {
            mergeContent(context, parseReadme(source, baseUrl, isWiki), dark)
        }
    }

    private fun parseReadme(source: String, baseUrl: String, isWiki: Boolean): String {
        val nameParser = NameParser(baseUrl)
        val owner = nameParser.username
        val repoName = nameParser.name
        val uri = Uri.parse(baseUrl)
        val paths = ArrayList(uri.pathSegments)
        val builder = StringBuilder()
        builder.append(owner).append("/").append(repoName).append("/")
        val containsMaster = paths.size > 3
        if (!containsMaster) {
            builder.append("master/")
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
        val baseLinkUrl = if (!isWiki) getLinkBaseUrl(baseUrl) else baseUrl
        return getParsedHtml(
            source,
            owner,
            repoName,
            if (!isWiki) builder.toString() else baseUrl,
            baseLinkUrl,
            isWiki
        )
    }

    private fun getParsedHtml(
        source: String, owner: String?, repoName: String?,
        builder: String, baseLinkUrl: String, isWiki: Boolean
    ): String {
        val document = Jsoup.parse(source, "")
        val imageElements = document.getElementsByTag("img")
        if (!imageElements.isEmpty()) {
            for (element in imageElements) {
                val src = element.attr("src")
                if (!(src.startsWith("http://") || src.startsWith("https://"))) {
                    val finalSrc: String = if (src.startsWith("/$owner/$repoName")) {
                        "https://raw.githubusercontent.com/$src"
                    } else {
                        "https://raw.githubusercontent.com/$builder$src"
                    }
                    element.attr("src", finalSrc)
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
                element.attr(
                    "href",
                    baseLinkUrl + if (isWiki && href.startsWith("wiki")) href.replaceFirst(
                        "wiki".toRegex(),
                        ""
                    ) else href
                )
            }
        }
        return document.html()
    }

    private fun getLinkBaseUrl(baseUrl: String): String {
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
            builder.append("blob/master/")
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
        return """<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
    <link rel="stylesheet" type="text/css" href="${getStyle(dark)}">
${getCodeStyle(context, dark)}
    <script src="./intercept-hash.js"></script>
</head>
<body>
$source
<script src="./intercept-touch.js"></script>
</body>
</html>
"""
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
${if (PrefGetter.getThemeType(context) == PrefGetter.AMLOD) "border: solid 1px $accentColor !important;\n" else ""}}
</style>"""
    }

    private fun getCodeBackgroundColor(context: Context): String {
        @ThemeType val themeType = PrefGetter.getThemeType()
        return if (themeType == PrefGetter.BLUISH) {
            "#" + Integer.toHexString(ViewHelper.getPrimaryDarkColor(context))
                .substring(2).uppercase(Locale.getDefault())
        } else "#" + Integer.toHexString(ViewHelper.getPrimaryColor(context))
            .substring(2).uppercase(Locale.getDefault())
    }
}