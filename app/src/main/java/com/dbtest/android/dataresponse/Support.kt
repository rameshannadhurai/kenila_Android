package com.dbtest.android.dataresponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Support(
    val text: String?,
    val url: String?
) : Parcelable