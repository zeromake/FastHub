package com.fastaccess.data.service

import com.fastaccess.data.dao.*
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Kosh on 07 May 2017, 1:01 PM
 */
interface ReviewService {
    @GET("repos/{owner}/{repo}/pulls/{number}/reviews/{id}")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html")
    fun getReview(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Long, @Path("id") id: Long
    ): Observable<ReviewModel>

    @GET("repos/{owner}/{repo}/pulls/{number}/reviews?per_page=100")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html")
    fun getReviews(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Long
    ): Observable<Pageable<ReviewModel>>

    @GET("repos/{owner}/{repo}/pulls/{number}/reviews/{id}/comments")
    @Headers("Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html")
    fun getReviewComments(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Long, @Path("id") reviewId: Long
    ): Observable<Pageable<ReviewCommentModel>>

    @GET("repos/{owner}/{repo}/pulls/{number}/comments?per_page=100")
    @Headers(
        "Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html, "
                + "application/vnd.github.squirrel-girl-preview"
    )
    fun getPrReviewComments(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Long
    ): Observable<Pageable<ReviewCommentModel>>

    @POST("/repos/{owner}/{repo}/pulls/{number}/comments")
    @Headers(
        "Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html, "
                + "application/vnd.github.squirrel-girl-preview"
    )
    fun submitComment(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Long, @Body body: CommentRequestModel
    ): Observable<ReviewCommentModel>

    @PATCH("/repos/{owner}/{repo}/pulls/comments/{id}")
    @Headers(
        "Accept: application/vnd.github.black-cat-preview+json, application/vnd.github.VERSION.html, "
                + "application/vnd.github.squirrel-girl-preview"
    )
    fun editComment(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("id") id: Long, @Body body: CommentRequestModel
    ): Observable<ReviewCommentModel>

    @DELETE("repos/{owner}/{repo}/pulls/comments/{id}")
    @Headers("Accept: application/vnd.github.black-cat-preview")
    fun deleteComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Observable<Response<Boolean>>

    @POST("repos/{owner}/{repo}/pulls/{number}/reviews")
    @Headers("Accept: application/vnd.github.black-cat-preview")
    fun submitPrReview(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Long, @Body body: ReviewRequestModel
    ): Observable<Response<ReviewModel>>
}