package com.loneoaktech.tests.safetynet

import android.app.Application
import timber.log.Timber
import java.lang.Appendable

/**
 * Created by BillH on 10/31/2018
 */
class SafetyNetApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }

}