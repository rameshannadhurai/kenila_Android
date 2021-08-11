package com.dbtest.android.data.networkdata

import com.dbtest.android.dataresponse.UserLists
import retrofit2.http.GET

interface ApiService {

    @GET("/api/users")
    suspend fun getUsersList(): UserLists

}