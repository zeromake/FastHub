package com.fastaccess.data.service

import retrofit2.http.POST
import com.fastaccess.data.dao.SlackInvitePostModel
import com.fastaccess.data.dao.SlackResponseModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Headers

/**
 * Created by Kosh on 01 May 2017, 1:04 AM
 */
interface SlackService {
    @POST("FastHubSlackInvite")
    @Headers("X-API-Key: MvFQyrJ9703DYmKHvk13I3agw3AdH8vh1lKbKGx4")
    fun invite(@Body body: SlackInvitePostModel): Observable<SlackResponseModel>
}