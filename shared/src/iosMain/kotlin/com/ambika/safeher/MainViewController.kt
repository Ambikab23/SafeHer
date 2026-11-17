
package com.ambika.safeher

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.ambika.safeher.database.DatabaseDriverFactory
import com.ambika.safeher.database.SafeHerDatabase

fun MainViewController(
    onPrepareSms: (String, String) -> Unit
) = ComposeUIViewController {

    val database = remember {
        SafeHerDatabase(
            DatabaseDriverFactory().createDriver()
        )
    }

    val locationHelper = remember {
        LocationHelper()
    }

    App(
        database = database,
        onRequestLocationPermission = { onResult ->
            locationHelper.requestCurrentLocation(onResult)
        },
        onPrepareSms = onPrepareSms
    )
}
