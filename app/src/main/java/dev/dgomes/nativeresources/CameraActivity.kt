package dev.dgomes.nativeresources

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import dev.dgomes.nativeresources.databinding.CameraActivityBinding
import java.text.SimpleDateFormat
import java.util.*

const val FILENAME_FORMAT = "dd-MM-yyyy-HH-mm-ss-SSS"

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: CameraActivityBinding
    private lateinit var cameraController: LifecycleCameraController
    private lateinit var previewView: PreviewView
    private lateinit var openCameraButton: Button
    private lateinit var takePictureButton: Button
    private lateinit var photoTaken: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CameraActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        previewView= binding.viewFinder
        openCameraButton = binding.openPictureButton
        takePictureButton = binding.takePictureButton
        photoTaken = binding.addedPhoto

        openCameraButton.setOnClickListener {
            if (!hasPermission(baseContext))
                pictureResult.launch(REQUIRED_PERMISSIONS)
            else openCamera()
        }

        takePictureButton.setOnClickListener {
            takePhoto()
            previewView.visibility = View.GONE
            openCameraButton.visibility = View.VISIBLE
            takePictureButton.visibility = View.GONE
            photoTaken.visibility = View.VISIBLE
        }
    }

    private fun openCamera() {
        photoTaken.visibility = View.GONE
        previewView.visibility = View.VISIBLE
        openCameraButton.visibility = View.GONE
        takePictureButton.visibility = View.VISIBLE

        cameraController = LifecycleCameraController(baseContext)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        previewView.controller = cameraController
    }

    private fun takePhoto() {
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.FRANCE)
            .format(System.currentTimeMillis())

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (SDK_INT > Build.VERSION_CODES.P)
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
        }

        val outputOptions = ImageCapture.OutputFileOptions
        .Builder(contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            .build()

        cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    Snackbar.make(
                        binding.root,
                        "Photo not taken",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo taken: ${output.savedUri}"
                    Snackbar.make(
                        binding.root,
                        msg,
                        Snackbar.LENGTH_SHORT
                    ).show()
                    photoTaken.setImageURI(output.savedUri)
                }
            }
        )
    }


    private val pictureResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value)
                    permissionGranted = false
            }
            if (!permissionGranted) Snackbar.make(
                binding.root,
                "Permission denied",
                Snackbar.LENGTH_SHORT
            ).show()
            else openCamera()
        }

    companion object {
        fun createIntent(context: Context) = Intent(context, CameraActivity::class.java)
        private val REQUIRED_PERMISSIONS = mutableListOf(android.Manifest.permission.CAMERA)
            .apply {
                if (SDK_INT <= Build.VERSION_CODES.P) add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }.toTypedArray()

        fun hasPermission(context: Context) = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}