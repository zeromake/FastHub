package com.fastaccess.data.service

import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.dao.PostReactionModel
import com.fastaccess.data.dao.ReactionsModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Kosh on 29 Mar 2017, 9:48 PM
 */
interface ReactionsService {
    @POST("repos/{owner}/{repo}/issues/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    fun postIssueCommentReaction(
        @Body body: PostReactionModel,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Observable<ReactionsModel>

    @POST("repos/{owner}/{repo}/issues/{number}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    fun postIssueReaction(
        @Body body: PostReactionModel,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Long
    ): Observable<ReactionsModel>

    @POST("repos/{owner}/{repo}/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    fun postCommitReaction(
        @Body body: PostReactionModel,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Observable<ReactionsModel>

    @DELETE("reactions/{id}")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    fun delete(@Path("id") id: Long): Observable<Response<Boolean>>

    @GET("repos/{owner}/{repo}/issues/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    fun getIssueCommentReaction(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long,
        @Query("content") content: String,
        @Query("page") page: Int
    ): Observable<Pageable<ReactionsModel>>

    @GET("repos/{owner}/{repo}/issues/{number}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    fun getIssueReaction(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") id: Long,
        @Query("content") content: String,
        @Query("page") page: Int
    ): Observable<Pageable<ReactionsModel>>

    @GET("repos/{owner}/{repo}/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    fun getCommitReaction(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long,
        @Query("content") content: String,
        @Query("page") page: Int
    ): Observable<Pageable<ReactionsModel>>

    @GET("repos/{owner}/{repo}/pulls/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview, application/vnd.github.black-cat-preview")
    fun getPullRequestReactions(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long,
        @Query("content") content: String,
        @Query("page") page: Int
    ): Observable<Pageable<ReactionsModel>>

    @POST("repos/{owner}/{repo}/pulls/comments/{id}/reactions")
    @Headers("Accept: application/vnd.github.squirrel-girl-preview, application/vnd.github.black-cat-preview")
    fun postCommentReviewReaction(
        @Body body: PostReactionModel,
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Observable<ReactionsModel>
}