package com.fastaccess.provider.emoji

import com.fastaccess.provider.emoji.EmojiManager.all
import com.fastaccess.provider.emoji.EmojiManager.getByUnicode
import com.fastaccess.provider.emoji.EmojiManager.getForAlias
import com.fastaccess.provider.emoji.EmojiManager.isEmoji
import java.util.*
import java.util.regex.Pattern

/**
 * Provides methods to parse strings with emojis.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
object EmojiParser {
    private val ALIAS_CANDIDATE_PATTERN = Pattern.compile("(?<=:)\\+?(\\w|\\||-)+(?=:)")

    /**
     * See [.parseToAliases] with the action
     * "PARSE"
     *
     * @param input the string to parse
     *
     * @return the string with the emojis replaced by their alias.
     */
    fun parseToAliases(input: String): String {
        return parseToAliases(input, FitzpatrickAction.PARSE)
    }

    /**
     * Replaces the emoji's unicode occurrences by one of their alias
     * (between 2 ':').<br></br>
     * Example: `😄` will be replaced by `:smile:`<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a PARSE action, a "|" will be
     * appendend to the alias, with the fitzpatrick type.<br></br>
     * Example: `👦🏿` will be replaced by
     * `:boy|type_6:`<br></br>
     * The fitzpatrick types are: type_1_2, type_3, type_4, type_5, type_6<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a REMOVE action, the modifier
     * will be deleted.<br></br>
     * Example: `👦🏿` will be replaced by `:boy:`<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a IGNORE action, the modifier
     * will be ignored.<br></br>
     * Example: `👦🏿` will be replaced by `:boy:🏿`<br></br>
     *
     * @param input             the string to parse
     * @param fitzpatrickAction the action to apply for the fitzpatrick modifiers
     *
     * @return the string with the emojis replaced by their alias.
     */
    private fun parseToAliases(
        input: String,
        fitzpatrickAction: FitzpatrickAction
    ): String {
        val emojiTransformer: EmojiTransformer =
            object : EmojiTransformer {
                override fun transform(unicodeCandidate: UnicodeCandidate): String {
                    return when (fitzpatrickAction) {
                        FitzpatrickAction.PARSE -> {
                            if (unicodeCandidate.hasFitzpatrick()) {
                                ":${unicodeCandidate.emoji.aliases[0]}|${unicodeCandidate.fitzpatrickType}:"
                            } else {
                                ":${unicodeCandidate.emoji.aliases[0]}:"
                            }
                        }
                        FitzpatrickAction.REMOVE -> return ":${unicodeCandidate.emoji.aliases[0]}:"
                        FitzpatrickAction.IGNORE -> return ":${unicodeCandidate.emoji.aliases[0]}:${unicodeCandidate.fitzpatrickUnicode}"
                    }
                }
            }
        return parseFromUnicode(input, emojiTransformer)
    }

    /**
     * Replaces the emoji's aliases (between 2 ':') occurrences and the html
     * representations by their unicode.<br></br>
     * Examples:<br></br>
     * `:smile:` will be replaced by `😄`<br></br>
     * `&#128516;` will be replaced by `😄`<br></br>
     * `:boy|type_6:` will be replaced by `👦🏿`
     *
     * @param input the string to parse
     *
     * @return the string with the aliases and html representations replaced by
     * their unicode.
     */
    @JvmStatic
    fun parseToUnicode(input: String): String {
        // Get all the potential aliases
        val candidates = getAliasCandidates(input)

        // Replace the aliases by their unicode
        var result = input
        for (candidate in candidates) {
            val emoji = getForAlias(candidate.alias)
            if (emoji != null) {
                if (emoji.supportsFitzpatrick() ||
                    !emoji.supportsFitzpatrick() && candidate.fitzpatrick == null
                ) {
                    var replacement: String? = emoji.unicode
                    replacement += candidate.fitzpatrick?.unicode
                    result = result.replace(
                        ":" + candidate.fullString + ":",
                        replacement!!
                    )
                }
            }
        }

        // Replace the html
        for (emoji in all!!) {
            result = result.replace(emoji.getHtmlHexadecimal(), emoji.unicode)
            result = result.replace(emoji.getHtmlDecimal(), emoji.unicode)
        }
        return result
    }

    private fun getAliasCandidates(input: String): List<AliasCandidate> {
        val candidates: MutableList<AliasCandidate> = ArrayList()
        var matcher = ALIAS_CANDIDATE_PATTERN.matcher(input)
        matcher = matcher.useTransparentBounds(true)
        while (matcher.find()) {
            val match = matcher.group()
            if (!match.contains("|")) {
                candidates.add(AliasCandidate(match, match, null))
            } else {
                val splitted = match.split("\\|").toTypedArray()
                if (splitted.size == 2 || splitted.size > 2) {
                    candidates.add(AliasCandidate(match, splitted[0], splitted[1]))
                } else {
                    candidates.add(AliasCandidate(match, match, null))
                }
            }
        }
        return candidates
    }

    /**
     * See [.parseToHtmlDecimal] with the action
     * "PARSE"
     *
     * @param input the string to parse
     *
     * @return the string with the emojis replaced by their html decimal
     * representation.
     */
    fun parseToHtmlDecimal(input: String): String {
        return parseToHtmlDecimal(input, FitzpatrickAction.PARSE)
    }

    /**
     * Replaces the emoji's unicode occurrences by their html representation.<br></br>
     * Example: `😄` will be replaced by `&#128516;`<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a PARSE or REMOVE action, the
     * modifier will be deleted from the string.<br></br>
     * Example: `👦🏿` will be replaced by
     * `&#128102;`<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a IGNORE action, the modifier
     * will be ignored and will remain in the string.<br></br>
     * Example: `👦🏿` will be replaced by
     * `&#128102;🏿`
     *
     * @param input             the string to parse
     * @param fitzpatrickAction the action to apply for the fitzpatrick modifiers
     *
     * @return the string with the emojis replaced by their html decimal
     * representation.
     */
    fun parseToHtmlDecimal(
        input: String,
        fitzpatrickAction: FitzpatrickAction
    ): String {
        val emojiTransformer: EmojiTransformer =
            object : EmojiTransformer {
                override fun transform(unicodeCandidate: UnicodeCandidate): String {
                    return when (fitzpatrickAction) {
                        FitzpatrickAction.PARSE, FitzpatrickAction.REMOVE -> unicodeCandidate.emoji.getHtmlDecimal()
                        FitzpatrickAction.IGNORE -> unicodeCandidate.emoji.getHtmlDecimal() +
                                unicodeCandidate.fitzpatrickUnicode
                    }
                }
            }
        return parseFromUnicode(input, emojiTransformer)
    }

    /**
     * See [.parseToHtmlHexadecimal] with the
     * action "PARSE"
     *
     * @param input the string to parse
     *
     * @return the string with the emojis replaced by their html hex
     * representation.
     */
    fun parseToHtmlHexadecimal(input: String): String {
        return parseToHtmlHexadecimal(input, FitzpatrickAction.PARSE)
    }

    /**
     * Replaces the emoji's unicode occurrences by their html hex
     * representation.<br></br>
     * Example: `👦` will be replaced by `&#x1f466;`<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a PARSE or REMOVE action, the
     * modifier will be deleted.<br></br>
     * Example: `👦🏿` will be replaced by
     * `&#x1f466;`<br></br>
     * <br></br>
     * When a fitzpatrick modifier is present with a IGNORE action, the modifier
     * will be ignored and will remain in the string.<br></br>
     * Example: `👦🏿` will be replaced by
     * `&#x1f466;🏿`
     *
     * @param input             the string to parse
     * @param fitzpatrickAction the action to apply for the fitzpatrick modifiers
     *
     * @return the string with the emojis replaced by their html hex
     * representation.
     */
    private fun parseToHtmlHexadecimal(
        input: String,
        fitzpatrickAction: FitzpatrickAction
    ): String {
        val emojiTransformer: EmojiTransformer =
            object : EmojiTransformer {
                override fun transform(unicodeCandidate: UnicodeCandidate): String {
                    return when (fitzpatrickAction) {
                        FitzpatrickAction.PARSE, FitzpatrickAction.REMOVE -> unicodeCandidate.emoji.getHtmlHexadecimal()
                        FitzpatrickAction.IGNORE -> unicodeCandidate.emoji.getHtmlHexadecimal() +
                                unicodeCandidate.fitzpatrickUnicode
                    }
                }
            }
        return parseFromUnicode(input, emojiTransformer)
    }

    /**
     * Removes all emojis from a String
     *
     * @param str the string to process
     *
     * @return the string without any emoji
     */
    fun removeAllEmojis(str: String): String {
        val emojiTransformer = object : EmojiTransformer {
            override fun transform(unicodeCandidate: UnicodeCandidate): String {
                return ""
            }
        }
        return parseFromUnicode(str, emojiTransformer)
    }

    /**
     * Removes a set of emojis from a String
     *
     * @param str            the string to process
     * @param emojisToRemove the emojis to remove from this string
     *
     * @return the string without the emojis that were removed
     */
    fun removeEmojis(
        str: String,
        emojisToRemove: Collection<Emoji?>
    ): String {
        val emojiTransformer: EmojiTransformer =
            object : EmojiTransformer {
                override fun transform(unicodeCandidate: UnicodeCandidate): String {
                    if (!emojisToRemove.contains(unicodeCandidate.emoji)) {
                        return unicodeCandidate.emoji.unicode +
                                unicodeCandidate.fitzpatrickUnicode
                    }
                    return ""
                }
            }
        return parseFromUnicode(str, emojiTransformer)
    }

    /**
     * Removes all the emojis in a String except a provided set
     *
     * @param str          the string to process
     * @param emojisToKeep the emojis to keep in this string
     *
     * @return the string without the emojis that were removed
     */
    fun removeAllEmojisExcept(
        str: String,
        emojisToKeep: Collection<Emoji?>
    ): String {
        val emojiTransformer: EmojiTransformer =
            object : EmojiTransformer {
                override fun transform(unicodeCandidate: UnicodeCandidate): String {
                    if (emojisToKeep.contains(unicodeCandidate.emoji)) {
                        return unicodeCandidate.emoji.unicode +
                                unicodeCandidate.fitzpatrickUnicode
                    }
                    return ""
                }
            }
        return parseFromUnicode(str, emojiTransformer)
    }

    /**
     * Detects all unicode emojis in input string and replaces them with the
     * return value of transformer.transform()
     *
     * @param input the string to process
     * @param transformer emoji transformer to apply to each emoji
     *
     * @return input string with all emojis transformed
     */
    private fun parseFromUnicode(
        input: String,
        transformer: EmojiTransformer
    ): String {
        var prev = 0
        val sb = StringBuilder()
        val replacements = getUnicodeCandidates(input)
        for (candidate in replacements) {
            sb.append(input.substring(prev, candidate.emojiStartIndex))
            sb.append(transformer.transform(candidate))
            prev = candidate.fitzpatrickEndIndex
        }
        return sb.append(input.substring(prev)).toString()
    }

    fun extractEmojis(input: String): List<String> {
        val emojis = getUnicodeCandidates(input)
        val result: MutableList<String> = ArrayList()
        for (emoji in emojis) {
            result.add(emoji.emoji.unicode)
        }
        return result
    }

    /**
     * Generates a list UnicodeCandidates found in input string. A
     * UnicodeCandidate is created for every unicode emoticon found in input
     * string, additionally if Fitzpatrick modifier follows the emoji, it is
     * included in UnicodeCandidate. Finally, it contains start and end index of
     * unicode emoji itself (WITHOUT Fitzpatrick modifier whether it is there or
     * not!).
     *
     * @param input String to find all unicode emojis in
     * @return List of UnicodeCandidates for each unicode emote in text
     */
    private fun getUnicodeCandidates(input: String): List<UnicodeCandidate> {
        val inputCharArray = input.toCharArray()
        val candidates: MutableList<UnicodeCandidate> = ArrayList()
        var i = 0
        while (i < input.length) {
            val emojiEnd = getEmojiEndPos(inputCharArray, i)
            if (emojiEnd != -1) {
                val emoji = getByUnicode(input.substring(i, emojiEnd))
                emoji ?: continue
                val fitzpatrickString =
                    if (emojiEnd + 2 <= input.length) String(inputCharArray, emojiEnd, 2) else null
                fitzpatrickString ?: continue
                val candidate = UnicodeCandidate(
                    emoji,
                    fitzpatrickString,
                    i
                )
                candidates.add(candidate)
                i = candidate.fitzpatrickEndIndex - 1
            }
            i++
        }
        return candidates
    }

    /**
     * Returns end index of a unicode emoji if it is found in text starting at
     * index startPos, -1 if not found.
     * This returns the longest matching emoji, for example, in
     * "\uD83D\uDC68\u200D\uD83D\uDC69\u200D\uD83D\uDC66"
     * it will find alias:family_man_woman_boy, NOT alias:man
     *
     * @param text the current text where we are looking for an emoji
     * @param startPos the position in the text where we should start looking for
     * an emoji end
     *
     * @return the end index of the unicode emoji starting at startPos. -1 if not
     * found
     */
    private fun getEmojiEndPos(text: CharArray, startPos: Int): Int {
        var best = -1
        for (j in startPos + 1..text.size) {
            val status = isEmoji(
                text.copyOfRange(startPos, j)
            )
            if (status.exactMatch()) {
                best = j
            } else if (status.impossibleMatch()) {
                return best
            }
        }
        return best
    }

    class UnicodeCandidate(val emoji: Emoji, fitzpatrick: String, startIndex: Int) {
        private val fitzpatrick: Fitzpatrick? = Fitzpatrick.fitzpatrickFromUnicode(fitzpatrick)
        val emojiStartIndex: Int = startIndex
        fun hasFitzpatrick(): Boolean {
            return fitzpatrick != null
        }

        val fitzpatrickType: String
            get() = if (hasFitzpatrick()) fitzpatrick!!.name.lowercase(Locale.getDefault()) else ""
        val fitzpatrickUnicode: String
            get() = if (hasFitzpatrick()) fitzpatrick!!.unicode else ""
        private val emojiEndIndex: Int
            get() = emojiStartIndex + emoji.unicode.length
        val fitzpatrickEndIndex: Int
            get() = emojiEndIndex + if (fitzpatrick != null) 2 else 0

    }

    internal class AliasCandidate(
        val fullString: String,
        val alias: String,
        fitzpatrickString: String?
    ) {
        var fitzpatrick: Fitzpatrick? = null

        init {
            fitzpatrick = if (fitzpatrickString == null) {
                null
            } else {
                Fitzpatrick.fitzpatrickFromType(fitzpatrickString)
            }
        }
    }

    /**
     * Enum used to indicate what should be done when a Fitzpatrick modifier is
     * found.
     */
    enum class FitzpatrickAction {
        /**
         * Tries to match the Fitzpatrick modifier with the previous emoji
         */
        PARSE,

        /**
         * Removes the Fitzpatrick modifier from the string
         */
        REMOVE,

        /**
         * Ignores the Fitzpatrick modifier (it will stay in the string)
         */
        IGNORE
    }

    interface EmojiTransformer {
        fun transform(unicodeCandidate: UnicodeCandidate): String?
    }
}