
package com.ambika.safeher

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.ambika.safeher.database.DatabaseDriverFactory
import com.ambika.safeher.database.SafeHerDatabase

class MainActivity : ComponentActivity() {

    private var locationResultCallback: ((Double?, Double?, Boolean) -> Unit)? = null

    private val permissionPrefs by lazy {
        getSharedPreferences("safeher_permissions", MODE_PRIVATE)
    }

    private var wasLocationPermissionRequestedBefore = false

    private val locationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->

            val locationGranted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            if (locationGranted) {
                LocationHelper.requestCurrentLocation(this) { location ->
                    if (location != null) {
                        locationResultCallback?.invoke(
                            location.latitude,
                            location.longitude,
                            false
                        )
                    } else {
                        Toast.makeText(
                            this,
                            "Unable to retrieve location. Please check your device location settings.",
                            Toast.LENGTH_LONG
                        ).show()

                        locationResultCallback?.invoke(null, null, false)
                    }

                    locationResultCallback = null
                }
            } else {
                val shouldShowRationale =
                    shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) || shouldShowRequestPermissionRationale(
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )

                val permanentlyDenied =
                    wasLocationPermissionRequestedBefore && !shouldShowRationale

                Toast.makeText(
                    this,
                    "Location permission denied. You can enable it in App Settings.",
                    Toast.LENGTH_LONG
                ).show()

                locationResultCallback?.invoke(
                    null,
                    null,
                    permanentlyDenied
                )

                locationResultCallback = null
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val driver = DatabaseDriverFactory(
            applicationContext
        ).createDriver()

        val database = SafeHerDatabase(driver)

        setContent {
            App(
                database = database,

                onRequestLocationPermission = { onResult ->
                    locationResultCallback = onResult

                    wasLocationPermissionRequestedBefore =
                        permissionPrefs.getBoolean(
                            "location_permission_requested",
                            false
                        )

                    permissionPrefs.edit()
                        .putBoolean("location_permission_requested", true)
                        .apply()

                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },

                onRegisterBackHandler = { onBack ->
                    val callback = object : OnBackPressedCallback(true) {
                        override fun handleOnBackPressed() {
                            onBack()
                        }
                    }

                    onBackPressedDispatcher.addCallback(
                        this,
                        callback
                    )

                    val unregister: () -> Unit = {
                        callback.remove()
                    }

                    unregister
                },

                onExitApp = {
                    finish()
                },

                onPrepareSms = { phoneNumber, message ->
                    val smsIntent = Intent(
                        Intent.ACTION_SENDTO,
                        Uri.parse("smsto:${Uri.encode(phoneNumber)}")
                    ).apply {
                        putExtra("sms_body", message)
                    }

                    try {
                        startActivity(smsIntent)
                    } catch (_: ActivityNotFoundException) {
                        Toast.makeText(
                            this,
                            "No messaging application is available on this device.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },

                onOpenAppSettings = {
                    val settingsIntent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", packageName, null)
                    )

                    try {
                        startActivity(settingsIntent)
                    } catch (_: ActivityNotFoundException) {
                        Toast.makeText(
                            this,
                            "Unable to open App Settings.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            )
        }
    }
}
