package com.dbtest.android.repository

import android.app.Application
import com.dbtest.android.data.networkdata.ApiService
import com.dbtest.android.data.networkdata.ServerClient

class ApiRepositoryImp : ApiRepository {

    private val apiService: ApiService = ServerClient.apiClient

    override suspend fun getApiUsersList() = apiService.getUsersList()
}