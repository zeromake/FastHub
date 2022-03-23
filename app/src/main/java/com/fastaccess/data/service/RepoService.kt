package com.fastaccess.data.service

import com.fastaccess.data.dao.*
import com.fastaccess.data.dao.model.*
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.*

/**
 * Created by Kosh on 10 Dec 2016, 3:16 PM
 */
interface RepoService {
    @GET
    @Headers("Accept: application/vnd.github.VERSION.raw")
    fun getFileAsStream(@Url url: String): Observable<String>

    @GET
    @Headers("Accept: application/vnd.github.html")
    fun getFileAsHtmlStream(@Url url: String): Observable<String>

    @POST("markdown")
    @Headers("Accept: application/vnd.github.v3+json")
    fun convertReadmeToHtml(@Body model: MarkdownModel): Observable<String>

    @GET("repos/{login}/{repoId}")
    @Headers("Accept: application/vnd.github.drax-preview+json, application/vnd.github.mercy-preview+json")
    fun getRepo(@Path("login") login: String, @Path("repoId") repoId: String): Observable<Repo>

    @DELETE("repos/{login}/{repoId}")
    fun deleteRepo(
        @Path("login") login: String,
        @Path("repoId") repoId: String
    ): Observable<Response<Boolean>>

    @GET
    @Headers("Accept: application/vnd.github.html")
    fun getReadmeHtml(@Url url: String): Observable<String>

    @GET("user/starred/{owner}/{repo}")
    fun checkStarring(
        @Path("owner") login: String,
        @Path("repo") repoId: String
    ): Observable<Response<Boolean>>

    @PUT("user/starred/{owner}/{repo}")
    fun starRepo(
        @Path("owner") login: String,
        @Path("repo") repoId: String
    ): Observable<Response<Boolean>>

    @DELETE("user/starred/{owner}/{repo}")
    fun unstarRepo(
        @Path("owner") login: String,
        @Path("repo") repoId: String
    ): Observable<Response<Boolean>>

    @POST("/repos/{owner}/{repo}/forks")
    fun forkRepo(@Path("owner") login: String, @Path("repo") repoId: String): Observable<Repo>

    @GET("repos/{owner}/{repo}/subscription")
    fun isWatchingRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<RepoSubscriptionModel>

    @PUT("user/subscriptions/{owner}/{repo}")
    fun watchRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<Response<Boolean>>

    @DELETE("user/subscriptions/{owner}/{repo}")
    fun unwatchRepo(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<Response<Boolean>>

    @GET("repos/{owner}/{repo}/commits")
    fun getCommits(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("sha") branch: String,
        @Query("page") page: Int
    ): Observable<Pageable<Commit>>

    @GET("repos/{owner}/{repo}/commits")
    fun getCommits(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Query("sha") branch: String,
        @Query("path") path: String,
        @Query("page") page: Int
    ): Observable<Pageable<Commit>>

    @GET("repos/{owner}/{repo}/releases")
    @Headers("Accept: application/vnd.github.VERSION.full+json")
    fun getReleases(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int
    ): Observable<Pageable<Release>>

    @GET("repos/{owner}/{repo}/releases/{id}")
    @Headers("Accept: application/vnd.github.VERSION.full+json")
    fun getRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Observable<Release>

    @GET("repos/{owner}/{repo}/releases/latest")
    fun getLatestRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<Release>

    @GET("repos/{owner}/{repo}/tags")
    @Headers("Accept: application/vnd.github.VERSION.full+json")
    fun getTagReleases(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int
    ): Observable<Pageable<Release>>

    @GET("repos/{owner}/{repo}/tags/{tag}")
    @Headers("Accept: application/vnd.github.VERSION.full+json")
    fun getTagRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("tag") tag: String
    ): Observable<Release>

    @GET("repos/{owner}/{repo}/contributors")
    fun getContributors(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int
    ): Observable<Pageable<User>>

    @GET("repos/{owner}/{repo}/commits/{sha}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun getCommit(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("sha") sha: String
    ): Observable<Commit>

    @GET("repos/{owner}/{repo}/commits/{sha}/comments")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun getCommitComments(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("sha") ref: String, @Query("page") page: Int
    ): Observable<Pageable<Comment>>

    @POST("repos/{owner}/{repo}/commits/{sha}/comments")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun postCommitComment(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("sha") ref: String, @Body model: CommentRequestModel
    ): Observable<Comment>

    @PATCH("repos/{owner}/{repo}/comments/{id}")
    @Headers("Accept: application/vnd.github.VERSION.full+json, application/vnd.github.squirrel-girl-preview")
    fun editCommitComment(
        @Path("owner") owner: String, @Path("repo") repo: String, @Path("id") id: Long,
        @Body body: CommentRequestModel
    ): Observable<Comment>

    @DELETE("repos/{owner}/{repo}/comments/{id}")
    fun deleteComment(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long
    ): Observable<Response<Boolean>>

    @GET("repos/{owner}/{repo}/contents/{path}")
    fun getRepoFiles(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path(value = "path", encoded = true) path: String,
        @Query("ref") ref: String
    ): Observable<okhttp3.ResponseBody>

    @GET("repos/{owner}/{repo}/git/trees/{sha}?recursive=1")
    fun getRepoTree(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("sha") sha: String
    ): Observable<TreeResponseModel>

    @GET("repos/{owner}/{repo}/labels?per_page=100")
    fun getLabels(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<Pageable<LabelModel>>

    @GET("repos/{owner}/{repo}/labels?per_page=100")
    fun getLabels(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int
    ): Observable<Pageable<LabelModel>>

    @POST("repos/{owner}/{repo}/labels")
    fun addLabel(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Body body: LabelModel
    ): Observable<LabelModel>

    @GET("repos/{owner}/{repo}/collaborators/{username}")
    fun isCollaborator(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Path("username") username: String
    ): Observable<Response<Boolean>>

    @GET("repos/{owner}/{repo}/collaborators?per_page=100")
    fun getCollaborator(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<Pageable<User>>

    @GET("repos/{owner}/{repo}/branches")
    fun getBranches(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Query("page") page: Int
    ): Observable<Pageable<BranchesModel>>

    @GET("repos/{owner}/{repo}/tags")
    fun getTags(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Query("page") page: Int
    ): Observable<Pageable<BranchesModel>>

    @GET("repos/{owner}/{repo}/milestones")
    fun getMilestones(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<Pageable<MilestoneModel>>

    @POST("repos/{owner}/{repo}/milestones")
    fun createMilestone(
        @Path("owner") owner: String, @Path("repo") repo: String,
        @Body create: CreateMilestoneModel
    ): Observable<MilestoneModel>

    @GET("repos/{owner}/{repo}/assignees")
    fun getAssignees(
        @Path("owner") owner: String,
        @Path("repo") repo: String
    ): Observable<Pageable<User>>

    @GET("repos/{owner}/{repo}/commits?per_page=1")
    fun getCommitCounts(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("sha") ref: String
    ): Observable<Pageable<Commit>>

    @GET("/repos/{owner}/{repo}/stargazers")
    fun getStargazers(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int
    ): Observable<Pageable<User>>

    @GET("/repos/{owner}/{repo}/subscribers")
    fun getWatchers(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int
    ): Observable<Pageable<User>>

    @GET("/repos/{owner}/{repo}/forks")
    fun getForks(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("page") page: Int
    ): Observable<Pageable<Repo>>

    @GET("repos/{owner}/{repo}/license")
    @Headers("Accept: application/vnd.github.html")
    fun getLicense(@Path("owner") owner: String, @Path("repo") repo: String): Observable<String>
}