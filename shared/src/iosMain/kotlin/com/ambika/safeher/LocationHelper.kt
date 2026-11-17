
package com.ambika.safeher

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.Foundation.NSError
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
class LocationHelper : NSObject(), CLLocationManagerDelegateProtocol {

    private val locationManager = CLLocationManager()

    private var locationCallback:
            ((Double?, Double?, Boolean) -> Unit)? = null

    init {
        locationManager.delegate = this
        locationManager.desiredAccuracy =
            platform.CoreLocation.kCLLocationAccuracyBest
    }

    fun requestCurrentLocation(
        onResult: (Double?, Double?, Boolean) -> Unit
    ) {
        locationCallback = onResult

        when (locationManager.authorizationStatus) {
            kCLAuthorizationStatusNotDetermined -> {
                locationManager.requestWhenInUseAuthorization()
            }

            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> {
                locationManager.requestLocation()
            }

            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted -> {
                finish(null, null, true)
            }

            else -> {
                finish(null, null, false)
            }
        }
    }

    override fun locationManagerDidChangeAuthorization(
        manager: CLLocationManager
    ) {
        when (manager.authorizationStatus) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> {
                manager.requestLocation()
            }

            kCLAuthorizationStatusDenied,
            kCLAuthorizationStatusRestricted -> {
                finish(null, null, true)
            }

            else -> Unit
        }
    }

    override fun locationManager(
        manager: CLLocationManager,
        didUpdateLocations: List<*>
    ) {
        val location = didUpdateLocations.lastOrNull() as? CLLocation

        if (location != null) {
            location.coordinate.useContents {
                finish(latitude, longitude, false)
            }
        } else {
            finish(null, null, false)
        }
    }

    override fun locationManager(
        manager: CLLocationManager,
        didFailWithError: NSError
    ) {
        finish(null, null, false)
    }

    private fun finish(
        latitude: Double?,
        longitude: Double?,
        permanentlyDenied: Boolean
    ) {
        val callback = locationCallback
        locationCallback = null

        callback?.invoke(
            latitude,
            longitude,
            permanentlyDenied
        )
    }
}
