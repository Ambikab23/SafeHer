
package com.ambika.safeher

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

object LocationHelper {

    @SuppressLint("MissingPermission")
    fun requestCurrentLocation(
        context: Context,
        onResult: (Location?) -> Unit
    ) {
        val finePermission = context.checkSelfPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarsePermission = context.checkSelfPermission(
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!finePermission && !coarsePermission) {
            onResult(null)
            return
        }

        val locationManager =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val providers = try {
            listOf(
                LocationManager.GPS_PROVIDER,
                LocationManager.NETWORK_PROVIDER
            ).filter { locationManager.isProviderEnabled(it) }
        } catch (_: Exception) {
            emptyList()
        }

        if (providers.isEmpty()) {
            onResult(null)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val completed = AtomicBoolean(false)
            val remaining = AtomicInteger(providers.size)

            providers.forEach { provider ->
                try {
                    locationManager.getCurrentLocation(
                        provider,
                        null,
                        context.mainExecutor
                    ) { location ->
                        if (location != null) {
                            if (completed.compareAndSet(false, true)) {
                                onResult(location)
                            }
                        } else if (remaining.decrementAndGet() == 0) {
                            if (completed.compareAndSet(false, true)) {
                                onResult(null)
                            }
                        }
                    }
                } catch (_: SecurityException) {
                    if (remaining.decrementAndGet() == 0) {
                        if (completed.compareAndSet(false, true)) {
                            onResult(null)
                        }
                    }
                }
            }
        } else {
            val completed = AtomicBoolean(false)
            val handler = Handler(Looper.getMainLooper())
            val provider = providers.first()

            val listener = object : LocationListener {

                override fun onLocationChanged(location: Location) {
                    if (completed.compareAndSet(false, true)) {
                        handler.removeCallbacksAndMessages(null)
                        locationManager.removeUpdates(this)
                        onResult(location)
                    }
                }

                @Deprecated("Deprecated by Android")
                override fun onStatusChanged(
                    provider: String?,
                    status: Int,
                    extras: Bundle?
                ) {
                }

                override fun onProviderEnabled(provider: String) {
                }

                override fun onProviderDisabled(provider: String) {
                }
            }

            handler.postDelayed({
                if (completed.compareAndSet(false, true)) {
                    locationManager.removeUpdates(listener)
                    onResult(null)
                }
            }, 15000)

            try {
                locationManager.requestSingleUpdate(
                    provider,
                    listener,
                    Looper.getMainLooper()
                )
            } catch (_: SecurityException) {
                if (completed.compareAndSet(false, true)) {
                    handler.removeCallbacksAndMessages(null)
                    onResult(null)
                }
            }
        }
    }
}
