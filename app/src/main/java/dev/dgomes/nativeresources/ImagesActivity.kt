package dev.dgomes.nativeresources

import android.Manifest.permission.*
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import dev.dgomes.nativeresources.databinding.ImagesActivityBinding


const val IMAGE_PERMISSION_CODE = 1

class ImagesActivity : AppCompatActivity() {

    private lateinit var binding: ImagesActivityBinding
    private var isImageAdded = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ImagesActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addImageButton.setOnClickListener {
            if (!isImageAdded){
                if (hasPermission()) Snackbar.make(binding.root, "Permission granted", Snackbar.LENGTH_SHORT).show()
                else pickImageFromGallery()
                binding.addedImage.visibility = View.VISIBLE
                binding.addImageButton.text = getString(R.string.removeImage)
                isImageAdded = true
            } else {
                binding.addImageButton.text = getString(R.string.addImage)
                binding.addedImage.visibility = View.GONE
                isImageAdded = false
            }
        }
    }

    private fun hasPermission() : Boolean {
        if (SDK_INT >= Build.VERSION_CODES.R) return Environment.isExternalStorageManager()
        return (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE)) == PackageManager.PERMISSION_GRANTED
    }

    private fun pickImageFromGallery() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                // Deprecated
                //startActivityForResult(intent, IMAGE_PICK_CODE)
                imageResult.launch(intent)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Intent.ACTION_PICK
                imageResult.launch(intent)
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    READ_EXTERNAL_STORAGE
                ), IMAGE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            IMAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) pickImageFromGallery()
                    else Snackbar.make(binding.root, "Permission denied", Snackbar.LENGTH_SHORT).show()
                } else Snackbar.make(binding.root, "Permission denied", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private val imageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
           if (it.resultCode == Activity.RESULT_OK) binding.addedImage.setImageURI(it.data?.data)
        }

    companion object {
        fun createIntent(context: Context) = Intent(context, ImagesActivity::class.java)
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//            if (SDK_INT >= Build.VERSION_CODES.R) {
//                if (Environment.isExternalStorageManager()) Snackbar.make(binding.root, "Permission granted", Snackbar.LENGTH_SHORT).show()
//                else Snackbar.make(binding.root, "Permission denied", Snackbar.LENGTH_SHORT).show()
//            } else Snackbar.make(binding.root, "Permission denied", Snackbar.LENGTH_SHORT).show()
//    }

//    private val requestPermissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestPermission()) { isGranted ->
//        if (isGranted) Snackbar.make(binding.root, "Permission granted", Snackbar.LENGTH_SHORT).show()
//        else Snackbar.make(binding.root, "Permission denied", Snackbar.LENGTH_SHORT).show()
//    }
//
//    private fun requestPermission() {
//        when {
//            ContextCompat.checkSelfPermission(this, CAMERA) == PackageManager.PERMISSION_GRANTED -> {
//            //TakePicture
//            }
//            ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA) -> {
//                layout.showSnackbar(
//                    view,
//                    getString(R.string.permission_required), Snackbar.LENGTH_INDEFINITE, getString(R.string.ok)
//                ) {
//                    requestPermissionLauncher.launch(CAMERA                    )
//                }
//            }
//            else -> {
//                requestPermissionLauncher.launch(CAMERA)
//            }
//        }
//    }
}