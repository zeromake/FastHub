package com.fastaccess.data.service

import com.fastaccess.data.dao.*
import com.fastaccess.data.dao.model.Comment
import com.fastaccess.data.dao.model.IssueEvent
import com.google.gson.JsonObject
import com.fastaccess.data.dao.model.Issue
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

interface IssueService {
    @GET("repos/{owner}/{repo}/issues")
    fun getRepositoryIssues(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Query("state") state: String, @Query("sort") sortBy: String,
        @Query("page") page: Int
    ): Observable<Pageable<Issue>>

    @GET("search/issues")
    fun getIssuesWithCount(
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Int
    ): Observable<Pageable<Issue>>

    @GET("user/issues")
    fun getUserIssues(
        @Query("page") page: Int,
        @Query("state") state: String
    ): Observable<Pageable<Issue>>

    @GET("repos/{owner}/{repo}/issues/{number}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun getIssue(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Int
    ): Observable<Issue>

    @GET("repos/{owner}/{repo}/issues/{issue_number}/events?per_page=100")
    fun getTimeline(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("issue_number") issue_number: Int
    ): Observable<Pageable<IssueEvent>>

    @GET("repos/{owner}/{repo}/issues/{issue_number}/timeline?per_page=100")
    @Headers(
        "Accept: application/vnd.github.mockingbird-preview,application/vnd.github.VERSION.full+json," +
                " application/vnd.github.squirrel-girl-preview"
    )
    fun getTimeline(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("issue_number") issue_number: Int, @Query("page") page: Int
    ): Observable<IssuesPageable<JsonObject>>

    @POST("repos/{owner}/{repo}/issues")
    fun createIssue(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Body issue: IssueRequestModel
    ): Observable<Issue>

    @PATCH("repos/{owner}/{repo}/issues/{number}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun editIssue(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Int,
        @Body issue: IssueRequestModel
    ): Observable<Issue>

    @Headers("Accept: application/vnd.github.sailor-v-preview+json")
    @PUT("repos/{owner}/{repo}/issues/{number}/lock")
    fun lockIssue(
        @Body body: LockIssuePrModel, @Path("owner") owner: String,
        @Path("repo") repo: String, @Path("number") number: Int
    ): Observable<Response<Boolean>>

    @DELETE("repos/{owner}/{repo}/issues/{number}/lock")
    fun unlockIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int
    ): Observable<Response<Boolean>>

    @GET("repos/{owner}/{repo}/issues/{number}/comments?per_page=100")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun getIssueComments(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Int,
        @Query("page") page: Int
    ): Observable<Pageable<Comment>>

    @GET("repos/{owner}/{repo}/issues/{number}/comments/{id}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun getIssueComment(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Int, @Path("id") id: Long
    ): Observable<Comment>

    @POST("repos/{owner}/{repo}/issues/{number}/comments")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun createIssueComment(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Int, @Body body: CommentRequestModel
    ): Observable<Comment>

    @PATCH("repos/{owner}/{repo}/issues/comments/{id}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun editIssueComment(
        @Path("owner") owner: String, @Path("repo") repo: String, @Path("id") id: Long,
        @Body body: CommentRequestModel
    ): Observable<Comment>

    @DELETE("repos/{owner}/{repo}/issues/comments/{id}")
    fun deleteIssueComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Observable<Response<Boolean>>

    @POST("repos/{owner}/{repo}/issues")
    fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body body: CreateIssueModel
    ): Observable<Issue>

    @PUT("repos/{owner}/{repo}/issues/{number}/labels")
    fun putLabels(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Int, @Body labels: List<String>
    ): Observable<Pageable<LabelModel>>

    @POST("repos/{owner}/{repo}/issues/{number}/assignees")
    fun putAssignees(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Int, @Body body: AssigneesRequestModel
    ): Observable<Issue>

    @HTTP(
        method = "DELETE",
        path = "repos/{owner}/{repo}/issues/{number}/assignees",
        hasBody = true
    )
    fun deleteAssignees(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Int, @Body body: AssigneesRequestModel
    ): Observable<Issue>

    @GET("/repos/{owner}/{repo}/issues/comments/{id}")
    @Headers(
        "Accept: application/vnd.github.mockingbird-preview,application/vnd.github.VERSION.full+json," +
                " application/vnd.github.squirrel-girl-preview"
    )
    fun getComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Observable<Comment>
}