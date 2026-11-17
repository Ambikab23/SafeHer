package com.ambika.safeher.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver

class DatabaseDriverFactory {

    fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            SafeHerDatabase.Schema,
            "safeher.db"
        )
    }
}