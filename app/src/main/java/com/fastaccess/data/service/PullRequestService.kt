package com.fastaccess.data.service

import com.fastaccess.data.dao.*
import com.fastaccess.data.dao.model.Commit
import com.fastaccess.data.dao.model.PullRequest
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Kosh on 15 Dec 2016, 10:21 PM
 */
interface PullRequestService {
    @GET("repos/{owner}/{repo}/pulls")
    fun getPullRequests(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Query("state") state: String, @Query("page") page: Int
    ): Observable<Pageable<PullRequest>>

    @GET("search/issues")
    fun getPullsWithCount(
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Int
    ): Observable<Pageable<PullRequest>>

    @GET("repos/{owner}/{repo}/pulls/{number}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun getPullRequest(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("number") number: Long
    ): Observable<PullRequest>

    @PUT("repos/{owner}/{repo}/pulls/{number}/merge")
    fun mergePullRequest(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Long, @Body body: MergeRequestModel
    ): Observable<MergeResponseModel>

    @GET("repos/{owner}/{repo}/pulls/{number}/commits")
    fun getPullRequestCommits(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Long,
        @Query("page") page: Int
    ): Observable<Pageable<Commit>>

    @GET("repos/{owner}/{repo}/pulls/{number}/files")
    fun getPullRequestFiles(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Long,
        @Query("page") page: Int
    ): Observable<Pageable<CommitFileModel>>

    @GET("repos/{owner}/{repo}/pulls/{number}/merge")
    fun hasPullRequestBeenMerged(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Long
    ): Observable<Response<Boolean>>

    @PATCH("repos/{owner}/{repo}/pulls/{number}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun editPullRequest(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Int,
        @Body issue: IssueRequestModel
    ): Observable<PullRequest>

    @PATCH("repos/{owner}/{repo}/issues/{number}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun editIssue(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Int,
        @Body issue: IssueRequestModel
    ): Observable<PullRequest>

    @POST("repos/{owner}/{repo}/issues/{number}/assignees")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun putAssignees(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Int, @Body body: AssigneesRequestModel
    ): Observable<PullRequest>

    @GET("repos/{owner}/{repo}/commits/{ref}/status")
    fun getPullStatus(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("ref") ref: String
    ): Observable<PullRequestStatusModel>

    @POST("repos/{owner}/{repo}/pulls/{number}/requested_reviewers")
    @Headers("Accept: application/vnd.github.black-cat-preview+json")
    fun putReviewers(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("number") number: Int, @Body body: AssigneesRequestModel
    ): Observable<PullRequest>
}