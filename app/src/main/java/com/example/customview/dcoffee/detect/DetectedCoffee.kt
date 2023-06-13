package com.example.customview.dcoffee.detect

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.customview.dcoffee.R
import com.example.customview.dcoffee.model.ViewModelFactory
import com.example.customview.dcoffee.preferences.UserPreferences
import com.example.customview.dcoffee.utils.uriToFile
import com.example.customview.dcoffee.camera.CameraActivity
import com.example.customview.dcoffee.databinding.ActivityAddCoffeeBinding
import com.example.customview.dcoffee.login.LoginActivity
import com.example.customview.dcoffee.main.MainActivity
import com.example.customview.dcoffee.utils.reduceFileImage
import com.example.customview.dcoffee.utils.rotateFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

@Suppress("DEPRECATION")
class DetectedCoffee : AppCompatActivity() {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: ActivityAddCoffeeBinding
    private lateinit var addViewModel: DetectedCoffeeViewModel
    private lateinit var token: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCoffeeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }
        setViewModel()
        setAction()
    }

    private fun setViewModel() {
        addViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreferences.getInstance(dataStore))
        )[DetectedCoffeeViewModel::class.java]
        addViewModel.getToken().observe(this) { session ->
            if (session.Login) {
                this.token = session.token
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        addViewModel.loading.observe(this) {
            showLoading(it)
        }

        addViewModel.errorMessage.observe(this) {
            when (it) {
                "Story created successfully" -> {
                    Toast.makeText(this@DetectedCoffee, getString(R.string.detectCreated),
                        Toast.LENGTH_SHORT).show()
                }
                "onFailure" -> {
                    Toast.makeText(this@DetectedCoffee, getString(R.string.failureMessage),
                        Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this@DetectedCoffee, getString(R.string.failReadImg),
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setAction() {
        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.buttonAdd.setOnClickListener { uploadImage() }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choosePictFirst))
        launcherIntentGallery.launch(chooser)
    }

    private var getFile: File? = null
    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = it.data?.getSerializableExtra("picture") as File
            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean
            getFile = myFile
            rotateFile(getFile as File, isBackCamera)
            val result = BitmapFactory.decodeFile(myFile.path)
            binding.previewImageView.setImageBitmap(result)
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
            result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@DetectedCoffee)
            getFile = myFile
            binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun uploadImage() {
        when {
            getFile == null -> {
                Toast.makeText(
                    this,
                    getString(R.string.choosePictFirst),
                    Toast.LENGTH_SHORT
                ).show()
            }
            binding.edDescription.text?.isNotEmpty() == false -> {
                Toast.makeText(
                    this,
                    getString(R.string.noDescription),
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
            else -> {
                binding.progressBarContainer.visibility = View.VISIBLE
                val file = reduceFileImage(getFile as File)

                val description =
                    binding.edDescription.text.toString().toRequestBody("text/plain".toMediaType())
                val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                    "photo",
                    file.name,
                    requestImageFile
                )
                addViewModel.uploadImage(imageMultipart, description, token)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.accessDenied),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            val i = Intent(this, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            finish()
        }
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}