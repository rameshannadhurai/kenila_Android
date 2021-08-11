package com.dbtest.android.dataresponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserLists(
        val `data`: List<UserDetails>?,
        val page: Int?,
        val per_page: Int?,
        val support: Support?,
        val total: Int?,
        val total_pages: Int?
) : Parcelable