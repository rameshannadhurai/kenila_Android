package com.dbtest.android.data.roomdata

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dbtest.android.dataresponse.UserDetails

@Dao
interface ImageVideoDAO {
    @Insert
    fun insert(imageVideo: ImageVideo)

    @Query("SELECT * FROM image_video_table WHERE userId =:id ORDER BY id DESC")
    fun getAllFiles(id: Int): LiveData<List<ImageVideo>>

    @Query("DELETE FROM image_video_table WHERE id =:id")
    fun deleteParticuler(id: Int)

    @Query("DELETE FROM image_video_table WHERE userId =:id")
    fun deleteAllFiles(id: Int)
}