package com.fastaccess.data.service

import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.dao.model.Event
import com.fastaccess.data.dao.model.Login
import com.fastaccess.data.dao.model.Repo
import com.fastaccess.data.dao.model.User
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Kosh on 08 Feb 2017, 8:54 PM
 */
interface UserRestService {
    @get:GET("user")
    val user: Observable<Login>

    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Observable<User>

    @GET("users/{username}/received_events")
    fun getReceivedEvents(
        @Path("username") userName: String,
        @Query("page") page: Int
    ): Observable<Pageable<Event>>

    @GET("users/{username}/events")
    fun getUserEvents(
        @Path("username") userName: String,
        @Query("page") page: Int
    ): Observable<Pageable<Event>>

    @GET("users/{username}/repos")
    fun getRepos(
        @Path("username") username: String,
        @QueryMap(encoded = true) filterParams: Map<String, String>,
        @Query("page") page: Int
    ): Observable<Pageable<Repo>>

    @GET("user/repos")
    fun getRepos(
        @QueryMap(encoded = true) filterParams: Map<String, String>,
        @Query(value = "page") page: Int
    ): Observable<Pageable<Repo>>

    @GET("users/{username}/starred")
    fun getStarred(
        @Path("username") username: String,
        @Query("page") page: Int
    ): Observable<Pageable<Repo>>

    @GET("users/{username}/starred?per_page=1")
    fun getStarredCount(@Path("username") username: String): Observable<Pageable<Repo>>

    @GET("users/{username}/following")
    fun getFollowing(
        @Path("username") username: String,
        @Query("page") page: Int
    ): Observable<Pageable<User>>

    @GET("users/{username}/followers")
    fun getFollowers(
        @Path("username") username: String,
        @Query("page") page: Int
    ): Observable<Pageable<User>>

    @GET("user/following/{username}")
    fun getFollowStatus(@Path("username") username: String): Observable<Response<Boolean>>

    @PUT("user/following/{username}")
    fun followUser(@Path("username") username: String): Observable<Response<Boolean>>

    @DELETE("user/following/{username}")
    fun unfollowUser(@Path("username") username: String): Observable<Response<Boolean>>

    @GET
    fun getContributions(@Url url: String): Observable<String>

    @GET("user/blocks/{username}")
    @Headers("Accept: application/vnd.github.giant-sentry-fist-preview+json")
    fun isUserBlocked(@Path("username") username: String): Observable<Response<Boolean>>

    @PUT("user/blocks/{username}")
    @Headers("Accept: application/vnd.github.giant-sentry-fist-preview+json")
    fun blockUser(@Path("username") username: String): Observable<Response<Boolean>>

    @DELETE("user/blocks/{username}")
    @Headers("Accept: application/vnd.github.giant-sentry-fist-preview+json")
    fun unBlockUser(@Path("username") username: String): Observable<Response<Boolean>>
}