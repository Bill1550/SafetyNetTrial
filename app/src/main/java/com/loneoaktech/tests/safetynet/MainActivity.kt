package com.loneoaktech.tests.safetynet

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.loneoaktech.tests.safetynet.gms.SafetyNetCaller
import com.loneoaktech.tests.safetynet.gms.VerifyAppsCaller
import com.loneoaktech.tests.safetynet.gms.isPlayServicesAvailable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class MainActivity : AppCompatActivity(), CoroutineScope {

    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()

        setContentView(R.layout.activity_main)

        Timber.i( "API_KEY=%s", BuildConfig.SAFETY_NET_API_KEY )

        Timber.i("Play services available: %b", isPlayServicesAvailable() )
    }


    override fun onResume() {
        super.onResume()

        statusText.text = "---- working ----"

        launch(Dispatchers.Main){
            val caller = SafetyNetCaller(this@MainActivity)

            val stmt = async(Dispatchers.IO) {  caller.getAttestation() }.await()

            Timber.i("Stmt: %s", stmt)

            val ok = stmt.ctsProfileMatch && stmt.basicIntegrity
            statusText.text = if (ok) "Device is OK" else "Devices is NOT OK"
            statusText.setTextColor( if (ok) Color.BLACK else Color.RED )
        }

        launch(Dispatchers.Main){
            val verifyCaller = VerifyAppsCaller(this@MainActivity)
            val available = async(Dispatchers.IO) {
//                delay(2000)
                verifyCaller.isVerifyAppsEnabled()
            }.await()

            Timber.i("verification available=%b", available)

            if (available) {
                val badApps = async(Dispatchers.IO) {

                    verifyCaller.getHarmfulApps()
                }.await()

                Timber.i("number harmful apps=%d", badApps.size )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job. cancel()
    }
}
