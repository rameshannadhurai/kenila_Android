package com.dbtest.android.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dbtest.android.databinding.ActivityMainBinding
import com.dbtest.android.dataresponse.UserDetails
import com.dbtest.android.listener.OnItemClickListener
import com.dbtest.android.ui.activity.userdetails.UserDetailsActivity
import com.dbtest.android.ui.adapter.UsersAdapter
import com.dbtest.android.utils.ConstantFields

class MainActivity : AppCompatActivity(), OnItemClickListener {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private lateinit var usersAdapter: UsersAdapter
    private lateinit var layoutManager: LinearLayoutManager

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        usersAdapter = UsersAdapter(this@MainActivity)
        layoutManager = LinearLayoutManager(this)
        binding.recyclerUserView.layoutManager = layoutManager
        binding.recyclerUserView.adapter = usersAdapter
        setUpViewModelObservers()
    }

    //Response observe
    private fun setUpViewModelObservers() {

        //Room DB Datas
        viewModel.allUsers.observe(this, {
            usersAdapter.setData(it)
        })

        //Api Response Data
        viewModel.userResponseLiveData.observe(this, {
            if (!it.data.isNullOrEmpty()) {
                viewModel.insertAllUsers(it.data)
                usersAdapter.setData(it.data)
            }
        })

        viewModel.loadingLiveData.observe(this, {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

        viewModel.errorLiveData.observe(this, {
            if (it != null) {
                Log.d(TAG, "setUpViewModelObservers: " + "Error......")
            }
        })

        //api hit
        viewModel.getUsersList()
    }

    override fun itemClick(user: UserDetails) {
        val intent = Intent(this@MainActivity, UserDetailsActivity::class.java)
        intent.putExtra(ConstantFields.EXTRA_USER_DATA, user)
        startActivity(intent)
    }
}