package com.fastaccess.provider.colors

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import com.annimon.stream.Stream
import com.fastaccess.App
import com.fastaccess.data.dao.LanguageColorModel
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.RxHelper
import com.fastaccess.ui.widgets.color.ColorGenerator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.disposables.Disposable
import java.io.IOException
import java.io.InputStreamReader

/**
 * Created by Kosh on 27 May 2017, 9:50 PM
 */
object ColorsProvider {
    private val POPULAR_LANG = Stream.of(
        "Java", "Kotlin", "JavaScript", "Python", "CSS", "PHP",
        "Ruby", "C++", "C", "Go", "Swift"
    ).toList() //predefined languages.
    private val colors: MutableMap<String, LanguageColorModel> = LinkedHashMap()

    @JvmStatic
    fun load(): Disposable? {
        // Todo Disposable handle
        if (colors.isEmpty()) {
            val disposable = RxHelper.safeObservable(
                Observable
                    .create { observableEmitter: ObservableEmitter<Any?> ->
                        try {
                            val type =
                                object : TypeToken<Map<String, LanguageColorModel>?>() {}.type
                            App.getInstance().assets.open("colors.json").use { stream ->
                                val gson = Gson()
                                JsonReader(InputStreamReader(stream)).use { reader ->
                                    val items: Map<String?, LanguageColorModel?> =
                                        gson.fromJson(reader, type)
                                    items.forEach {
                                        if (it.key != null) {
                                            colors[it.key!!] = it.value!!
                                        }
                                    }
                                    observableEmitter.onNext("")
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            observableEmitter.onError(e)
                        }
                        observableEmitter.onComplete()
                    })
                .subscribe({}) { obj: Throwable -> obj.printStackTrace() }
            return disposable
        }
        return null
    }

    fun languages(): List<String> {
        val lang = colors.asSequence().map { it.key }.toMutableList()
        lang.add(0, "All Languages")
        lang.addAll(1, POPULAR_LANG)
        return lang
    }

    fun getColor(lang: String): LanguageColorModel? {
        return colors[lang]
    }

    @JvmStatic
    @ColorInt
    fun getColorAsColor(lang: String, context: Context): Int {
        val color = getColor(lang)
        var langColor = ColorGenerator.getColor(context, lang)
        if (color != null && !InputHelper.isEmpty(color.color)) {
            try {
                langColor = Color.parseColor(color.color)
            } catch (ignored: Exception) {
            }
        }
        return langColor
    }
}