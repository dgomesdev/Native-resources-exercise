package dev.dgomes.nativeresources

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events.*
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import dev.dgomes.nativeresources.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setContentView(binding.root)

        binding.cvAppointment.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CONTENT_URI)
                .putExtra(TITLE, "Appointment example")
                .putExtra(EVENT_LOCATION, "Online")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis())
                .putExtra(
                    CalendarContract.EXTRA_EVENT_END_TIME,
                    System.currentTimeMillis() + (60 * 60 * 1000)
                )

            startActivity(intent)
        }

        binding.cvContacts.setOnClickListener {
            startActivity(ContactsActivity.createIntent(this))
        }

        binding.cvImages.setOnClickListener {
            startActivity(ImagesActivity.createIntent(this))
        }

        binding.cvCamera.setOnClickListener {
            startActivity(CameraActivity.createIntent(this))
        }

        binding.cvLocation.setOnClickListener {
            startActivity(LocationActivity.createIntent(this))
        }

    }

}