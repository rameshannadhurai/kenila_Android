package com.dbtest.android.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dbtest.android.R
import com.dbtest.android.data.roomdata.ImageVideo
import com.dbtest.android.databinding.ActivityFileBinding
import com.dbtest.android.utils.ConstantFields
import com.dbtest.android.utils.imageGlideLoad
import java.io.File

class OpenFileActivity : AppCompatActivity() {
    private val TAG = "OpenFileActivity"
    private lateinit var binding: ActivityFileBinding
    private var imageVideo: ImageVideo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageVideo = intent.getParcelableExtra(ConstantFields.EXTRA_FILE_DATA)!!
        if (imageVideo?.type.equals("image")) {
            binding.photoView.visibility = View.VISIBLE
            imageGlideLoad(binding.photoView, imageVideo?.file)
        } else {
            Log.d(TAG, "onCreate: " + "VideoView Load")
            val file = File(imageVideo?.file.toString())
            binding.videoView.setVideoPath(file.absolutePath)
            val mediaController = MediaController(this)
            mediaController.setAnchorView(binding.videoView)
            //specify the location of media file
            binding.videoView.setMediaController(mediaController)
            binding.videoView.requestFocus()
            binding.videoView.visibility = View.VISIBLE
            binding.videoView.start()
        }
    }
}