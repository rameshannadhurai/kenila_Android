package com.dbtest.android.ui.activity.userdetails

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dbtest.android.BuildConfig
import com.dbtest.android.R
import com.dbtest.android.data.roomdata.ImageVideo
import com.dbtest.android.databinding.ActivityUserDetailsBinding
import com.dbtest.android.dataresponse.UserDetails
import com.dbtest.android.listener.OnItemFileClickListener
import com.dbtest.android.ui.activity.OpenFileActivity
import com.dbtest.android.ui.adapter.FileAdapter
import com.dbtest.android.utils.ConstantFields
import com.dbtest.android.utils.imageGlideLoad
import com.dbtest.android.utils.toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.util.*

class UserDetailsActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener,
    OnItemFileClickListener {

    private val TAG = "UserDetailsActivity"

    private lateinit var binding: ActivityUserDetailsBinding
    private lateinit var viewModel: UserDetailsViewModel
    private lateinit var mMap: GoogleMap
    private var manager: LocationManager? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var filtype: Int = 0
    private var mPhotoFile: File? = null
    private var userDetails: UserDetails? = null
    private lateinit var usersAdapter: FileAdapter
    private lateinit var layoutManager: LinearLayoutManager

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this).get(UserDetailsViewModel::class.java)
        userDetails = intent.getParcelableExtra(ConstantFields.EXTRA_USER_DATA)!!
        usersAdapter = FileAdapter(this@UserDetailsActivity)
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerUserView.layoutManager = layoutManager
        binding.recyclerUserView.adapter = usersAdapter
        userDetails?.let {
            binding.userId.text = getString(R.string.user_id).plus(" : ").plus(it.id)
            binding.userName.text = it.first_name.plus(" ").plus(it.last_name)
            binding.userEmail.text = it.email
            imageGlideLoad(binding.userImage, it.avatar)
        }
        setUpObserver()
        binding.fabAdd.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val animals = arrayOf("Camera", "Gallery", "Take Video")
            builder.setItems(
                animals
            ) { dialog: DialogInterface?, which: Int ->
                when (which) {
                    0 -> openFileType(0)
                    1 -> openFileType(1)
                    2 -> openFileType(2)
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
        manager = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getLocation()
    }

    private fun setUpObserver() {
        viewModel.getAllFiles(userDetails!!).observe(this, Observer {
            if (it != null) {
                usersAdapter.setData(it)
            }
        })
    }

    override fun itemClick(item: ImageVideo) {
        val intent = Intent(this@UserDetailsActivity, OpenFileActivity::class.java)
        intent.putExtra(ConstantFields.EXTRA_FILE_DATA, item)
        startActivity(intent)
    }

    private fun newFile(i: Int): File? {
        val cal = Calendar.getInstance()
        val timeInMillis = cal.timeInMillis
        var mFileName = ""
        when (i) {
            0 -> mFileName = "$timeInMillis.jpeg"
            1 -> mFileName = "$timeInMillis.mp4"
        }
        val mFilePath = getFilePath()
        try {
            val newFile = File(mFilePath!!.absolutePath, mFileName)
            newFile.createNewFile()
            return newFile
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun getFilePath(): File? {
        return getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    }

    fun getRealPathFromURI(contentUri: Uri?): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri!!, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun openFileType(type: Int) {
        filtype = type
        checkPermission.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private val checkPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            it.entries.forEach { it1 ->
                Log.e("DEBUG", "${it1.key} = ${it1.value}")
                if (!it1.value) {
                    shouldShowRequestPermissionRationale(it1.key)
                    return@forEach
                }
            }
            when (filtype) {
                0 -> startCamera()
                1 -> takeGallery.launch("image/*")
                2 -> captureVideo()
            }
        }

    private fun startCamera() {
        mPhotoFile = newFile(0)
        if (mPhotoFile != null) {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                mPhotoFile!!
            )
            registerTakePicture.launch(photoURI)
        }
    }


    private val registerTakePicture =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                lifecycleScope.launch {
                    val imageVideo = ImageVideo(
                        0,
                        userDetails?.id,
                        "image",
                        "file",
                        mPhotoFile?.toString(),
                        mPhotoFile?.name,
                        latitude,
                        longitude
                    )
                    viewModel.insert(imageVideo)
                }
            } else {
                toast("Capture Failed")
            }
        }
    private val videoLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                activityResult.data?.data?.let {
                    lifecycleScope.launch {
                        val imageVideo = ImageVideo(
                            0,
                            userDetails?.id,
                            "video",
                            "file",
                            mPhotoFile?.toString(),
                            mPhotoFile?.name,
                            latitude,
                            longitude
                        )
                        viewModel.insert(imageVideo)
                    }
                }
            } else {
                toast("Record Failed")
            }
        }

    private fun captureVideo() {
        mPhotoFile = newFile(1)
        if (mPhotoFile != null) {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                BuildConfig.APPLICATION_ID + ".provider",
                mPhotoFile!!
            )
            Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }.also { takeVideoIntent ->
                takeVideoIntent.resolveActivity(packageManager)?.also {
                    videoLauncher.launch(takeVideoIntent)
                }
            }
        }
    }

    private val takeGallery = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            val file = getFileFromUri(contentResolver, it, cacheDir)
            lifecycleScope.launch {
                val imageVideo = ImageVideo(
                    0,
                    userDetails?.id,
                    "image",
                    "file",
                    file.name,
                    file.toString(),
                    latitude,
                    longitude
                )
                viewModel.insert(imageVideo)
            }
        }
    }


    //Link:-  https://stackoverflow.com/questions/8646246/uri-from-intent-action-get-content-into-file
    private fun getFileFromUri(contentResolver: ContentResolver, uri: Uri, directory: File): File {
        val file = File.createTempFile("suffix", "prefix", directory)
        file.outputStream().use { contentResolver.openInputStream(uri)?.copyTo(it) }
        return file
    }


    private fun getLocation() {
        if (allPermissionsGranted()) {
            if (!isLocationTurnedOn())
                createGPSOnRequest()
            else registerForLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    fun createGPSOnRequest() {
        val locationRequest = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest!!)
            .setAlwaysShow(true)
            .setNeedBle(true)

        val client: SettingsClient = LocationServices.getSettingsClient(this@UserDetailsActivity)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            Log.i(TAG, "Task success")
            try {
                task.getResult(ApiException::class.java)
            } catch (e: ApiException) {
                when (e.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                        try {
                            (e as ResolvableApiException).apply {
                                val intentSenderRequest =
                                    IntentSenderRequest.Builder(e.resolution).build()
                                launcherLocation.launch(intentSenderRequest)
                            }
                        } catch (e: IntentSender.SendIntentException) {
                            Log.i(TAG, e.toString())
                        } catch (e: ClassCastException) {
                            Log.i(TAG, e.toString())
                        }
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> Log.i(
                        TAG,
                        "settings change unavailable"
                    )
                }
            }
        }
        task.addOnFailureListener { exception ->
            Log.i(TAG, "Task Failure $exception")
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest =
                        IntentSenderRequest.Builder(exception.resolution).build()
                    launcherLocation.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.i(TAG, sendEx.toString())
                }
            }
        }
    }

    private val launcherLocation =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                registerForLocationUpdates()
            } else {
                createGPSOnRequest()
            }
        }

    @SuppressLint("MissingPermission")
    private fun registerForLocationUpdates() {
        manager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000L, 10f, this)
        manager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000L, 10f, this)
    }

    private fun isLocationTurnedOn() =
        manager!!.isProviderEnabled(LocationManager.GPS_PROVIDER) or manager!!.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )

    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        manager?.removeUpdates(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    override fun onLocationChanged(location: Location) {
        if(location!=null){
            val latLng = LatLng(location.latitude, location.longitude)
            latitude = latLng.latitude
            longitude = latLng.latitude
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Current Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        }
    }

    override fun onProviderDisabled(provider: String) {

    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                getLocation()
            } else {
                toast("Permissions not granted by the user.")
            }
        }
    }

}
