package com.loneoaktech.tests.safetynet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.loneoaktech.tests.safetynet.gms.SafetyNetCaller
import com.loneoaktech.tests.safetynet.gms.isPlayServicesAvailable
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.i( "API_KEY=%s", BuildConfig.SAFETY_NET_API_KEY )

        Timber.i("Play services available: %b", isPlayServicesAvailable() )
    }


    override fun onResume() {
        super.onResume()


        val caller = SafetyNetCaller(this)

        caller.getAttestation()
    }
}
