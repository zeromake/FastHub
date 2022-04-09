package com.fastaccess.data.service

import com.fastaccess.data.dao.Pageable
import retrofit2.http.GET
import com.fastaccess.data.dao.TeamsModel
import com.fastaccess.data.entity.Event
import com.fastaccess.data.entity.Repo
import com.fastaccess.data.entity.User
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

/**
 * Created by Kosh on 22 Mar 2017, 6:44 PM
 */
interface OrganizationService {
    @GET("orgs/{org}/members/{username}")
    fun isMember(
        @Path("org") org: String,
        @Path("username") username: String
    ): Observable<Response<Boolean>>

    @GET("orgs/{org}")
    fun getOrganization(@Path("org") org: String): Observable<User>

    @get:GET("user/orgs?per_page=200")
    val myOrganizations: Observable<Pageable<User>>

    @GET("users/{user}/orgs")
    fun getMyOrganizations(@Path("user") user: String): Observable<Pageable<User>>

    @GET("orgs/{org}/teams")
    fun getOrgTeams(
        @Path("org") org: String,
        @Query("page") page: Int
    ): Observable<Pageable<TeamsModel>>

    @GET("orgs/{org}/members")
    fun getOrgMembers(
        @Path("org") org: String,
        @Query("page") page: Int
    ): Observable<Pageable<User>>

    @GET("teams/{id}/members")
    fun getTeamMembers(
        @Path("id") id: Long,
        @Query("page") page: Int
    ): Observable<Pageable<User>>

    @GET("teams/{id}/repos")
    fun getTeamRepos(@Path("id") id: Long, @Query("page") page: Int): Observable<Pageable<Repo>>

    @GET("users/{username}/events/orgs/{org}")
    fun getReceivedEvents(
        @Path("username") userName: String,
        @Path("org") org: String, @Query("page") page: Int
    ): Observable<Pageable<Event>>

    @GET("orgs/{org}/repos")
    fun getOrgRepos(
        @Path("org") org: String,
        @QueryMap(encoded = true) filterParams: Map<String, String>,
        @Query("page") page: Int
    ): Observable<Pageable<Repo>>
}