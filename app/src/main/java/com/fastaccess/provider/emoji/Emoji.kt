package com.fastaccess.provider.emoji

import java.nio.charset.StandardCharsets
import java.util.*

/**
 * This class represents an emoji.<br></br>
 * <br></br>
 * This object is immutable so it can be used safely in a multithreaded context.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
class Emoji(
    /**
     * Returns the description of the emoji
     *
     * @return the description
     */
    val description: String,
    private val supportsFitzpatrick: Boolean,
    aliases: List<String>,
    tags: List<String>,
    vararg bytes: Byte
) {
    /**
     * Returns the aliases of the emoji
     *
     * @return the aliases (unmodifiable)
     */
    val aliases: List<String> = Collections.unmodifiableList(aliases)

    /**
     * Returns the tags of the emoji
     *
     * @return the tags (unmodifiable)
     */
    val tags: List<String> = Collections.unmodifiableList(tags)
    val unicode: String
    private val htmlDec: String
    private val htmlHex: String

    /**
     * Method to replace String.join, since it was only introduced in java8
     *
     * @param array
     * the array to be concatenated
     * @return concatenated String
     */
    private fun stringJoin(array: Array<String?>, count: Int): String {
        val joined = StringBuilder()
        for (i in 0 until count) joined.append(array[i])
        return joined.toString()
    }

    /**
     * Returns wether the emoji supports the Fitzpatrick modifiers or not
     *
     * @return true if the emoji supports the Fitzpatrick modifiers
     */
    fun supportsFitzpatrick(): Boolean {
        return supportsFitzpatrick
    }

    /**
     * Returns the unicode representation of the emoji associated with the
     * provided Fitzpatrick modifier.<br></br>
     * If the modifier is null, then the result is similar to
     * [Emoji.getUnicode]
     *
     * @param fitzpatrick
     * the fitzpatrick modifier or null
     * @return the unicode representation
     * @throws UnsupportedOperationException
     * if the emoji doesn't support the Fitzpatrick modifiers
     */
    fun getUnicode(fitzpatrick: Fitzpatrick?): String {
        if (!supportsFitzpatrick()) {
            throw UnsupportedOperationException(
                "Cannot get the unicode with a fitzpatrick modifier, " +
                        "the emoji doesn't support fitzpatrick."
            )
        } else if (fitzpatrick == null) {
            return this.unicode
        }
        return this.unicode + fitzpatrick.unicode
    }

    /**
     * Returns the HTML decimal representation of the emoji
     *
     * @return the HTML decimal representation
     */
    fun getHtmlDecimal(): String {
        return htmlDec
    }

    /**
     * @return the HTML hexadecimal representation
     */
    @Deprecated("identical to {@link #getHtmlHexadecimal()} for backwards-compatibility. Use that instead.",
        ReplaceWith("getHtmlHexadecimal()")
    )
    fun getHtmlHexidecimal(): String {
        return getHtmlHexadecimal()
    }

    /**
     * Returns the HTML hexadecimal representation of the emoji
     *
     * @return the HTML hexadecimal representation
     */
    fun getHtmlHexadecimal(): String {
        return htmlHex
    }

    override fun equals(other: Any?): Boolean {
        return other is Emoji && other.unicode == unicode
    }

    override fun hashCode(): Int {
        return unicode.hashCode()
    }

    /**
     * Returns the String representation of the Emoji object.<br></br>
     * <br></br>
     * Example:<br></br>
     * `Emoji {
     * description='smiling face with open mouth and smiling eyes',
     * supportsFitzpatrick=false,
     * aliases=[smile],
     * tags=[happy, joy, pleased],
     * unicode='😄',
     * htmlDec='&#128516;',
     * htmlHex='&#x1f604;'
     * }`
     *
     * @return the string representation
     */
    override fun toString(): String {
        return "Emoji{" +
                "description='" + description + '\'' +
                ", supportsFitzpatrick=" + supportsFitzpatrick +
                ", aliases=" + aliases +
                ", tags=" + tags +
                ", unicode='" + unicode + '\'' +
                ", htmlDec='" + htmlDec + '\'' +
                ", htmlHex='" + htmlHex + '\'' +
                '}'
    }

    /**
     * Constructor for the Emoji.
     *
     * @param description
     * The description of the emoji
     * @param supportsFitzpatrick
     * Whether the emoji supports Fitzpatrick modifiers
     * @param aliases
     * the aliases for this emoji
     * @param tags
     * the tags associated with this emoji
     * @param bytes
     * the bytes that represent the emoji
     */
    init {
        var count = 0
        unicode = String(bytes, StandardCharsets.UTF_8)
        val stringLength = unicode.length
        val pointCodes = arrayOfNulls<String>(stringLength)
        val pointCodesHex = arrayOfNulls<String>(stringLength)
        var offset = 0
        while (offset < stringLength) {
            val codePoint = unicode.codePointAt(offset)
            pointCodes[count] = String.format(Locale.getDefault(), "&#%d;", codePoint)
            pointCodesHex[count++] = String.format("&#x%x;", codePoint)
            offset += Character.charCount(codePoint)
        }
        htmlDec = stringJoin(pointCodes, count)
        htmlHex = stringJoin(pointCodesHex, count)
    }
}