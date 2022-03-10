package com.fastaccess.provider.emoji

import java.util.HashMap

class EmojiTrie(emojis: Collection<Emoji>) {
    private val root: Node = Node()

    /**
     * Checks if sequence of chars contain an emoji.
     *
     * @param sequence
     * Sequence of char that may contain emoji in full or partially.
     * @return &lt;li&gt; Matches.EXACTLY if char sequence in its entirety is an emoji &lt;/li&gt; &lt;li&gt; Matches.POSSIBLY if char sequence
     * matches prefix of an emoji &lt;/li&gt; &lt;li&gt; Matches.IMPOSSIBLE if char sequence matches no emoji or prefix of an emoji &lt;/li&gt;
     */
    fun isEmoji(sequence: CharArray?): Matches {
        if (sequence == null) {
            return Matches.POSSIBLY
        }
        var tree: Node? = root
        for (c in sequence) {
            if (!tree!!.hasChild(c)) {
                return Matches.IMPOSSIBLE
            }
            tree = tree.getChild(c)
        }
        return if (tree!!.isEndOfEmoji) Matches.EXACTLY else Matches.POSSIBLY
    }

    /**
     * Finds Emoji instance from emoji unicode
     *
     * @param unicode
     * unicode of emoji to get
     * @return Emoji instance if unicode matches and emoji, null otherwise.
     */
    fun getEmoji(unicode: String): Emoji? {
        var tree: Node? = root
        for (c in unicode.toCharArray()) {
            if (!tree!!.hasChild(c)) {
                return null
            }
            tree = tree.getChild(c)
        }
        return tree?.emoji
    }

    enum class Matches {
        EXACTLY, POSSIBLY, IMPOSSIBLE;

        fun exactMatch(): Boolean {
            return this == EXACTLY
        }

        fun impossibleMatch(): Boolean {
            return this == IMPOSSIBLE
        }

        fun possibleMatch(): Boolean {
            return this == POSSIBLY
        }
    }

    private inner class Node {
        private val children: MutableMap<Char, Node> = HashMap()
        var emoji: Emoji? = null

        fun hasChild(child: Char): Boolean {
            return children.containsKey(child)
        }

        fun addChild(child: Char) {
            children[child] = Node()
        }

        fun getChild(child: Char): Node? {
            return children[child]
        }

        val isEndOfEmoji: Boolean
            get() = emoji != null
    }

    init {
        for (emoji in emojis) {
            var tree: Node? = root
            for (c in emoji.unicode.toCharArray()) {
                if (!tree!!.hasChild(c)) {
                    tree!!.addChild(c)
                }
                tree = tree!!.getChild(c)
            }
            tree!!.emoji = emoji
        }
    }
}