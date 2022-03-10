package com.fastaccess.provider.emoji

import com.fastaccess.App
import com.fastaccess.helper.RxHelper
import com.fastaccess.provider.emoji.EmojiLoader.loadEmojis
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.io.IOException

/**
 * Holds the loaded emojis and provides search functions.
 *
 * @author Vincent DURMONT [vdurmont@gmail.com]
 */
object EmojiManager {
    private const val PATH = "emojis.json"
    private val EMOJIS_BY_ALIAS: MutableMap<String, Emoji> = HashMap()
    private val EMOJIS_BY_TAG: MutableMap<String, MutableSet<Emoji>?> = HashMap()
    @JvmStatic
    var all: List<Emoji>? = null
        private set
    private var EMOJI_TRIE: EmojiTrie? = null
    @JvmStatic
    fun load() {
        RxHelper.safeObservable(Observable.fromCallable {
            try {
                val stream = App.getInstance().assets.open(PATH)
                val emojis = loadEmojis(stream)
                all = emojis
                for (emoji in emojis) {
                    for (tag in emoji.tags) {
                        if (EMOJIS_BY_TAG[tag] == null) {
                            EMOJIS_BY_TAG[tag] = HashSet()
                        }
                        EMOJIS_BY_TAG[tag]!!.add(emoji)
                    }
                    for (alias in emoji.aliases) {
                        EMOJIS_BY_ALIAS[alias] = emoji
                    }
                }
                EMOJI_TRIE = EmojiTrie(emojis)
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            ""
        }).subscribeOn(Schedulers.io()).subscribe()
    }

    fun getForTag(tag: String?): Set<Emoji>? {
        return if (tag == null) {
            null
        } else EMOJIS_BY_TAG[tag]
    }

    @JvmStatic
    fun getForAlias(alias: String?): Emoji? {
        return if (alias == null) {
            null
        } else EMOJIS_BY_ALIAS[trimAlias(alias)]
    }

    private fun trimAlias(alias: String): String {
        var result = alias
        if (result.startsWith(":")) {
            result = result.substring(1)
        }
        if (result.endsWith(":")) {
            result = result.substring(0, result.length - 1)
        }
        return result
    }

    @JvmStatic
    fun getByUnicode(unicode: String?): Emoji? {
        return if (unicode == null) {
            null
        } else EMOJI_TRIE!!.getEmoji(unicode)
    }

    fun isEmoji(string: String?): Boolean {
        return string != null &&
                EMOJI_TRIE!!.isEmoji(string.toCharArray()).exactMatch()
    }

    fun isOnlyEmojis(string: String?): Boolean {
        return string != null && EmojiParser.removeAllEmojis(string).isEmpty()
    }

    @JvmStatic
    fun isEmoji(sequence: CharArray?): EmojiTrie.Matches {
        return EMOJI_TRIE!!.isEmoji(sequence)
    }

    val allTags: Collection<String>
        get() = EMOJIS_BY_TAG.keys
}