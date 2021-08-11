package com.dbtest.android.listener

import com.dbtest.android.dataresponse.UserDetails

interface OnItemClickListener {
    fun itemClick(user: UserDetails)
}