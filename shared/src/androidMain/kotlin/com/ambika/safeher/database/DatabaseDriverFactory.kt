package com.ambika.safeher.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

class DatabaseDriverFactory(
    private val context: Context
) {
    fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            SafeHerDatabase.Schema,
            context,
            "safeher.db"
        )
    }
}