package com.fastaccess.utils

class Optional<T> {
    private var value: T? = null

    private constructor()
    private constructor(value: T) {
        this.value = value
    }

    fun isEmpty(): Boolean {
        return this.value == null
    }

    fun get(): T? {
        return value
    }

    fun or(): T {
        return this.value!!
    }

    fun orElse(): T? {
        return this.value
    }

    fun orElse(other: T?): T? {
        return if (this.value != null) this.value else other
    }

    companion object {
        val EMPTY = Optional<Any>()
        fun <T> empty(): Optional<T> {
            @Suppress("UNCHECKED_CAST")
            return EMPTY as Optional<T>
        }

        fun <T> of(value: T): Optional<T> {
            return Optional(value)
        }

        fun <T> ofNullable(value: T?): Optional<T> {
            return if (value == null) empty() else of(value)
        }
    }
}