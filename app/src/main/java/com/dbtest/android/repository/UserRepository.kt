package com.dbtest.android.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.dbtest.android.data.roomdata.AppRoomDataBasse
import com.dbtest.android.data.roomdata.ImageVideo
import com.dbtest.android.data.roomdata.ImageVideoDAO
import com.dbtest.android.data.roomdata.UserDAO
import com.dbtest.android.dataresponse.UserDetails
import com.dbtest.android.utils.loggerDebug
import com.dbtest.android.utils.subscribeOnBackground

class UserRepository(application: Application) {
    private val TAG = "UserRepository"
    private var userDao: UserDAO
    private var imageVideoDAO: ImageVideoDAO
    private val database = AppRoomDataBasse.getInstance(application)

    init {
        userDao = database.userDao()
        imageVideoDAO = database.imageVideoDAO()
    }

    fun insert(user: UserDetails) {
        subscribeOnBackground {
            val userDetails = userDao.isExits(user.id!!)
            loggerDebug(TAG, user.id.toString())
            if (userDetails) {
                update(user)
            } else {
                userDao.insert(user)
            }
        }
    }


    fun insertAll(user: List<UserDetails>) {
        subscribeOnBackground {
            user.forEach {
                val userAdd = userDao.isExits(it.id!!)
                loggerDebug(TAG, it.id.toString())
                if (userAdd) {
                    update(it)
                } else {
                    userDao.insertAll(user)
                }
            }

        }
    }

    fun update(user: UserDetails) {
        subscribeOnBackground {
            userDao.update(user)
        }
    }

    fun delete(user: UserDetails) {
        subscribeOnBackground {
            userDao.delete(user)
        }
    }

    fun deleteAllUsers() {
        subscribeOnBackground {
            userDao.deleteAllUsers()
        }
    }

    fun getAllUsers(): LiveData<List<UserDetails>> {
        return userDao.getAllUsers()
    }


    fun insertFile(imageVideo: ImageVideo) {
        subscribeOnBackground {
            imageVideoDAO.insert(imageVideo)
        }
    }

    fun delete(imageVideo: ImageVideo) {
        subscribeOnBackground {
            imageVideoDAO.deleteParticuler(imageVideo.id!!)
        }
    }

    fun getAllFiles(user: UserDetails): LiveData<List<ImageVideo>> {
        return imageVideoDAO.getAllFiles(user.id!!)
    }

}