package com.dbtest.android.repository

import com.dbtest.android.dataresponse.UserLists

interface ApiRepository {
    suspend fun getApiUsersList(): UserLists
}