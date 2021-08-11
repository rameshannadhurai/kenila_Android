package com.dbtest.android.utils

import android.app.Activity
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dbtest.android.R

fun imageGlideLoad(view: ImageView, url: String?) {
    url?.let {
        Glide.with(view.context).load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.ic_launcher_background).into(view)
    }
}

fun Activity.toast(msg: String?) = runOnUiThread {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}