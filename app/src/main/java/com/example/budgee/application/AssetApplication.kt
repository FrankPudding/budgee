package com.example.budgee.application

import android.app.Application
import com.example.budgee.db.AppDatabase

class AssetApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}