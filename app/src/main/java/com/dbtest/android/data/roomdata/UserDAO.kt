package com.dbtest.android.data.roomdata

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dbtest.android.dataresponse.UserDetails

@Dao
interface UserDAO {
    @Insert
    fun insert(user: UserDetails)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(users: List<UserDetails>)

    @Update
    fun update(user: UserDetails)

    @Delete
    fun delete(user: UserDetails)

    @Query("DELETE FROM user_table")
    fun deleteAllUsers()

    @Query("SELECT * FROM user_table ORDER BY id DESC")
    fun getAllUsers(): LiveData<List<UserDetails>>

    @Query("SELECT * FROM user_table WHERE id =:id LIMIT 1")
    fun isExits(id: Int): Boolean

}