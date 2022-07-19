package com.fastaccess.data.service

import androidx.annotation.StringDef
import com.fastaccess.data.dao.NotificationSubscriptionBodyModel
import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.dao.RepoSubscriptionModel
import com.fastaccess.data.entity.Comment
import com.fastaccess.data.entity.Notification
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Kosh on 19 Feb 2017, 6:34 PM
 */
interface NotificationService {
    @StringDef(ISSUE_THREAD_CLASS, PULL_REQUEST_THREAD_CLASS)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class ThreadClass

    @StringDef(SUBSCRIBE, MUTE)
    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    annotation class ThreadId

    @GET("notifications")
    fun getNotifications(@Query("since") date: String): Observable<Pageable<Notification>>

    @get:GET("notifications?all=true&per_page=200")
    val allNotifications: Observable<Pageable<Notification>>

    @PATCH("notifications/threads/{id}")
    fun markAsRead(@Path("id") id: String): Observable<Response<Boolean>>

    @GET
    fun getComment(@Url commentUrl: String): Observable<Comment>

    @GET("notifications/threads/{id}/subscription")
    fun isSubscribed(@Path("id") id: Long): Observable<RepoSubscriptionModel>

    @DELETE("notifications/threads/{id}/subscription")
    fun unSubscribe(@Path("id") id: Long): Observable<Response<Boolean>>

    @PUT("notifications/threads/{id}/subscription")
    fun subscribe(
        @Path("id") id: Long,
        @Body body: NotificationSubscriptionBodyModel
    ): Observable<Response<Boolean>>

    companion object {
        const val SUBSCRIPTION_URL = "https://github.com/notifications/thread"
        const val ISSUE_THREAD_CLASS = "Issue"
        const val PULL_REQUEST_THREAD_CLASS = "PullRequest"
        const val SUBSCRIBE = "subscribe"
        const val MUTE = "mute"
        const val UTF8 = "✓"
    }
}