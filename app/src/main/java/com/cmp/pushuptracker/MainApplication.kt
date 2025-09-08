package com.cmp.pushuptracker

import android.app.Application
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase (safe even if already initialized by Google Services)
        FirebaseApp.initializeApp(this)

        // Disable automatic Crashlytics collection in debug builds
        if (!BuildConfig.DEBUG) {
            Firebase.crashlytics.isCrashlyticsCollectionEnabled = true
        } else {
            Firebase.crashlytics.isCrashlyticsCollectionEnabled = false
        }    }
}
