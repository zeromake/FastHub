package com.fastaccess.utils

import android.view.View
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit


@JvmOverloads
fun View.setOnThrottleClickListener(
    windowDuration: Long = 100,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    listener: (view: View) -> Unit
): Disposable {
    return Observable.create(ObservableOnSubscribe<View> { emitter ->
        this.setOnClickListener {
            if (!emitter.isDisposed) {
                emitter.onNext(it)
            } else {
                it.setOnClickListener(null)
            }
        }
    }).throttleFirst(windowDuration, unit)
        .subscribe { listener(it) }
}

@JvmOverloads
fun List<View>.setOnThrottleClickListener(
    windowDuration: Long = 100,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    listener: (view: View) -> Unit
): Disposable {
    return Observable.create(ObservableOnSubscribe<View> { emitter ->
        val onClick: (view: View) -> Unit = {
            if (!emitter.isDisposed) {
                emitter.onNext(it)
            } else {
                it.setOnClickListener(null)
            }
        }
        this.forEach { view ->
            view.setOnClickListener(onClick)
        }
    }).throttleFirst(windowDuration, unit)
        .subscribe { listener(it) }
}
