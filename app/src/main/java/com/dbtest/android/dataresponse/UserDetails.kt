package com.dbtest.android.dataresponse

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "user_table")
@Parcelize
data class UserDetails(
    @PrimaryKey(autoGenerate = true) val userId: Int? = null,
    val avatar: String?,
    val email: String?,
    val first_name: String?,
    val id: Int?,
    val last_name: String?
) : Parcelable