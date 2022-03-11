package com.fastaccess.data.service

import retrofit2.http.POST
import okhttp3.RequestBody
import com.fastaccess.data.dao.ImgurReponseModel
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.Query

/**
 * Created by Kosh on 15 Apr 2017, 8:06 PM
 */
interface ImgurService {
    @POST("image")
    fun postImage(
        @Query("title") title: String,
        @Body body: RequestBody
    ): Observable<ImgurReponseModel>
}
