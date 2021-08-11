package com.dbtest.android.data.roomdata

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "image_video_table")
@Parcelize
class ImageVideo(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val userId: Int?,
    val type: String?,
    val file_type: String?,
    val file: String?,
    var file_name: String?,
    val latitude: Double?,
    val longtitude: Double?,
) : Parcelable
