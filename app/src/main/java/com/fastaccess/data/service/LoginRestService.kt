package com.fastaccess.data.service

import com.fastaccess.data.entity.Login
import com.fastaccess.data.dao.AuthModel
import com.fastaccess.data.dao.AccessTokenModel
import io.reactivex.Observable
import retrofit2.http.*

interface LoginRestService {
    @GET("user")
    fun loginAccessToken(): Observable<Login>

    @POST("authorizations")
    fun login(@Body authModel: AuthModel): Observable<AccessTokenModel>

    @FormUrlEncoded
    @POST("access_token")
    @Headers("Accept: application/json")
    fun getAccessToken(
        @Field("code") code: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("state") state: String,
        @Field("redirect_uri") redirectUrl: String
    ): Observable<AccessTokenModel>
}