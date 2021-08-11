package com.dbtest.android.data.roomdata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dbtest.android.dataresponse.UserDetails

@Database(entities = [UserDetails::class,ImageVideo::class], version = 1)
abstract class AppRoomDataBasse : RoomDatabase() {

    abstract fun userDao(): UserDAO

    abstract fun imageVideoDAO(): ImageVideoDAO

    companion object {
        private var instance: AppRoomDataBasse? = null

        @Synchronized
        fun getInstance(ctx: Context): AppRoomDataBasse {
            if(instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext, AppRoomDataBasse::class.java,
                    "kenil_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .allowMainThreadQueries()
                    .build()
            return instance!!
        }

        private val roomCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }
        }
    }

}