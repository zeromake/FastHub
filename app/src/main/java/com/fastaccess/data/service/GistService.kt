package com.fastaccess.data.service

import com.fastaccess.data.dao.CommentRequestModel
import com.fastaccess.data.dao.CreateGistModel
import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.data.dao.model.Gist
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Kosh on 20 Nov 2016, 10:28 AM
 */
interface GistService {
    @GET
    @Headers("Accept: application/vnd.github.VERSION.raw")
    fun getGistFile(@Url url: String): Observable<String>

    @POST("gists")
    fun createGist(@Body gistBody: CreateGistModel): Observable<Gist>

    @PATCH("gists/{id}")
    fun editGist(@Body gistBody: CreateGistModel, @Path("id") id: String): Observable<Gist>

    @DELETE("gists/{id}")
    fun deleteGist(@Path("id") id: String): Observable<Response<Boolean>>

    @GET("gists/public")
    fun getPublicGists(
        @Query("per_page") perPage: Int,
        @Query("page") page: Int
    ): Observable<Pageable<Gist>>

    @GET("gists")
    fun getMyGists(@Query("page") page: Int): Observable<Pageable<Gist>>

    @GET("users/{username}/gists")
    fun getUserGists(
        @Path("username") username: String,
        @Query("page") page: Int
    ): Observable<Pageable<Gist>>

    @GET("gists/{id}")
    fun getGist(@Path("id") id: String): Observable<Gist>

    @GET("gists/{id}/comments")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun getGistComments(
        @Path("id") id: String,
        @Query("page") page: Int
    ): Observable<Pageable<Comment>>

    @POST("gists/{gist_id}/comments")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun createGistComment(
        @Path("gist_id") gistId: String,
        @Body body: CommentRequestModel
    ): Observable<Comment>

    @PATCH("gists/{gist_id}/comments/{id}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun editGistComment(
        @Path("gist_id") gistId: String,
        @Path("id") id: Long,
        @Body body: CommentRequestModel
    ): Observable<Comment>

    @DELETE("gists/{gist_id}/comments/{id}")
    fun deleteGistComment(
        @Path("gist_id") gistId: String,
        @Path("id") id: Long
    ): Observable<Response<Boolean>>

    @GET("gists/{gist_id}/star")
    fun checkGistStar(@Path("gist_id") gistId: String): Observable<Response<Boolean>>

    @PUT("gists/{gist_id}/star")
    fun starGist(@Path("gist_id") gistId: String): Observable<Response<Boolean>>

    @DELETE("gists/{gist_id}/star")
    fun unStarGist(@Path("gist_id") gistId: String): Observable<Response<Boolean>>

    @POST("gists/{gist_id}/forks")
    fun forkGist(@Path("gist_id") gistId: String): Observable<Response<Gist>>

    @GET("/gists/starred")
    fun getStarredGists(@Query("page") page: Int): Observable<Pageable<Gist>>
}