package com.fastaccess.data.service

import com.fastaccess.data.dao.Pageable
import com.fastaccess.data.dao.SearchCodeModel
import com.fastaccess.data.dao.model.Issue
import com.fastaccess.data.dao.model.Repo
import com.fastaccess.data.dao.model.User
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Kosh on 08 Dec 2016, 9:07 PM
 */
interface SearchService {
    @GET("search/repositories")
    fun searchRepositories(
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ): Observable<Pageable<Repo>>

    @GET("search/code")
    fun searchCode(
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ): Observable<Pageable<SearchCodeModel>>

    @GET("search/issues")
    fun searchIssues(
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ): Observable<Pageable<Issue>>

    @GET("search/users")
    fun searchUsers(
        @Query(value = "q", encoded = true) query: String,
        @Query("page") page: Long
    ): Observable<Pageable<User>>
}