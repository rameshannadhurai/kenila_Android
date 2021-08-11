package com.dbtest.android.ui.activity.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dbtest.android.data.roomdata.ImageVideo
import com.dbtest.android.dataresponse.UserDetails
import com.dbtest.android.dataresponse.UserLists
import com.dbtest.android.repository.ApiRepositoryImp
import com.dbtest.android.repository.UserRepository
import com.dbtest.android.utils.NetworkHelper
import com.dbtest.android.utils.loggerDebug
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val TAG = "MainViewModel"
    private val mApiRepositoryImp = ApiRepositoryImp()
    private val repository = UserRepository(app)
    private var _allUsers =  repository.getAllUsers()

    private val _userResponseLiveData = MutableLiveData<UserLists>()
    private val _loadingLiveData = MutableLiveData<Boolean>(false)
    val errorLiveData = MutableLiveData<Any>(null)

    val userResponseLiveData: LiveData<UserLists> get() = _userResponseLiveData
    val loadingLiveData: LiveData<Boolean> get() = _loadingLiveData
    val allUsers: LiveData<List<UserDetails>> get() = _allUsers

    fun getUsersList() {
        _loadingLiveData.value = true
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            mApiRepositoryImp.getApiUsersList().let { res ->
                _userResponseLiveData.postValue(res)
                _loadingLiveData.postValue(false)
            }
        }
    }

    //Response Error Handle
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
        _loadingLiveData.postValue(false)
        errorLiveData.postValue(NetworkHelper.handleThrowable(t))
    }


    fun insert(user: UserDetails) {
        loggerDebug(TAG, user.id.toString())
        repository.insert(user)
    }

    fun insertAllUsers(user: List<UserDetails>) {
        repository.insertAll(user)
    }

    fun update(user: UserDetails) {
        repository.update(user)
    }

    fun delete(user: UserDetails) {
        repository.delete(user)
    }

    fun deleteAllUsers() {
        repository.deleteAllUsers()
    }

}