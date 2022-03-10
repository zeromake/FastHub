package com.fastaccess.provider.emoji

import kotlin.Throws
import org.json.JSONArray
import com.fastaccess.provider.emoji.EmojiLoader
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.Exception
import java.lang.StringBuilder
import java.nio.charset.StandardCharsets
import java.util.ArrayList

/**
 * Loads the emojis from a JSON database.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
internal object EmojiLoader {
    @JvmStatic
    @Throws(IOException::class)
    fun loadEmojis(stream: InputStream): List<Emoji> {
        try {
            val emojisJSON = JSONArray(inputStreamToString(stream))
            val emojis: MutableList<Emoji> = ArrayList(emojisJSON.length())
            for (i in 0 until emojisJSON.length()) {
                val emoji = buildEmojiFromJSON(emojisJSON.getJSONObject(i))
                if (emoji != null) {
                    emojis.add(emoji)
                }
            }
            return emojis
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return emptyList()
    }

    @Throws(IOException::class)
    private fun inputStreamToString(stream: InputStream): String {
        val sb = StringBuilder()
        val isr = InputStreamReader(stream, StandardCharsets.UTF_8)
        val br = BufferedReader(isr)
        var read: String?
        while (br.readLine().also { read = it } != null) {
            sb.append(read)
        }
        br.close()
        return sb.toString()
    }

    @Throws(Exception::class)
    private fun buildEmojiFromJSON(json: JSONObject): Emoji? {
        if (!json.has("emoji")) {
            return null
        }
        val bytes = json.getString("emoji").toByteArray(StandardCharsets.UTF_8)
        var description: String? = null
        if (json.has("description")) {
            description = json.getString("description")
        }
        var supportsFitzpatrick = false
        if (json.has("supports_fitzpatrick")) {
            supportsFitzpatrick = json.getBoolean("supports_fitzpatrick")
        }
        val aliases = jsonArrayToStringList(json.getJSONArray("aliases"))
        val tags = jsonArrayToStringList(json.getJSONArray("tags"))
        return Emoji(description!!, supportsFitzpatrick, aliases, tags, *bytes)
    }

    private fun jsonArrayToStringList(array: JSONArray): List<String> {
        val strings: MutableList<String> = ArrayList(array.length())
        try {
            for (i in 0 until array.length()) {
                strings.add(array.getString(i))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strings
    }
}