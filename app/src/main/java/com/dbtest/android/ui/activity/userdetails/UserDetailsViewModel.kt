package com.dbtest.android.ui.activity.userdetails

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dbtest.android.data.roomdata.ImageVideo
import com.dbtest.android.dataresponse.UserDetails
import com.dbtest.android.repository.UserRepository
import kotlinx.coroutines.launch

class UserDetailsViewModel(app: Application) : AndroidViewModel(app) {
    private val TAG = "UserDetailsViewModel"
    private val repository = UserRepository(app)
    private val _imageVideoLiveData = MutableLiveData<List<ImageVideo>>()

    val imageVideoLiveData: LiveData<List<ImageVideo>> get() = _imageVideoLiveData

    fun insert(imageVideo: ImageVideo) {
        repository.insertFile(imageVideo)
    }

    fun getAllFiles(userDetails: UserDetails):LiveData<List<ImageVideo>> {
        return repository.getAllFiles(userDetails)
    }
}