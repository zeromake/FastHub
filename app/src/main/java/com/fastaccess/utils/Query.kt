package com.fastaccess.utils

import io.objectbox.Box
import io.objectbox.Property
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder
import io.reactivex.Observable
import io.reactivex.Single

fun <T> QueryBuilder<T>.equal(property: Property<T>, value: String): QueryBuilder<T> {
    this.equal(property, value, QueryBuilder.StringOrder.CASE_SENSITIVE)
    return this
}


fun <T> QueryBuilder<T>.notEqual(property: Property<T>, value: String): QueryBuilder<T> {
    this.notEqual(property, value, QueryBuilder.StringOrder.CASE_SENSITIVE)
    return this
}

fun <T, P : Any> Box<T>.toObservable(task: (box: Box<T>) -> P): Observable<P> {
    return Observable.create {
        val tmp = task(this)
        it.onNext(tmp)
        it.onComplete()
    }
}


fun <T, P : Any> Query<T>.toObservable(task: (query: Query<T>) -> P): Observable<P> {
    return Observable.create {
        val tmp = task(this)
        it.onNext(tmp)
        it.onComplete()
    }
}


fun <T, P : Any> Box<T>.toSingle(task: (box: Box<T>) -> P): Single<P> {
    return Single.create {
        val tmp = task(this)
        it.onSuccess(tmp)
    }
}


fun <T, P : Any> Query<T>.toSingle(task: (query: Query<T>) -> P): Single<P> {
    return Single.create {
        val tmp = task(this)
        it.onSuccess(tmp)
    }
}

fun <T, P> Box<T>.toObservableOptional(task: (box: Box<T>) -> P?): Observable<Optional<P>> {
    return Observable.create {
        val tmp = task(this)
        it.onNext(Optional.ofNullable(tmp))
        it.onComplete()
    }
}

fun <T, P> Box<T>.toSingleOptional(task: (box: Box<T>) -> P?): Single<Optional<P>> {
    return Single.create {
        val tmp = task(this)
        it.onSuccess(Optional.ofNullable(tmp))
    }
}


fun <T, P> Query<T>.toObservableOptional(task: (query: Query<T>) -> P?): Observable<Optional<P>> {
    return Observable.create {
        val tmp = task(this)
        it.onNext(Optional.ofNullable(tmp))
        it.onComplete()
    }
}

fun <T, P> Query<T>.toSingleOptional(task: (query: Query<T>) -> P?): Single<Optional<P>> {
    return Single.create {
        val tmp = task(this)
        it.onSuccess(Optional.ofNullable(tmp))
    }
}
