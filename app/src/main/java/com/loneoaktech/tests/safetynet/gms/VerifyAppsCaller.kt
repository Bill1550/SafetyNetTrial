package com.loneoaktech.tests.safetynet.gms

import android.content.Context
import com.google.android.gms.safetynet.HarmfulAppsData
import com.google.android.gms.safetynet.SafetyNet
import com.loneoaktech.tests.safetynet.utility.nullIfFalse
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by BillH on 11/1/2018
 */
class VerifyAppsCaller(private val context: Context) {

    suspend fun isVerifyAppsEnabled(): Boolean = suspendCoroutine { continuation ->
        SafetyNet.getClient(context)
                .isVerifyAppsEnabled
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        continuation.resume( task.result?.isVerifyAppsEnabled ?: false)
                    } else {
                        continuation.resumeWithException(Exception("Error getting version info"))
                    }
                }
    }

    suspend fun getHarmfulApps(): List<HarmfulAppsData> = suspendCoroutine { continuation ->

        SafetyNet.getClient(context)
                .listHarmfulApps()
                .addOnCompleteListener {  task ->
                    task.result?.nullIfFalse { task.isSuccessful }?.let { result ->
                        Timber.i("Num harmful apps found=%d", result.harmfulAppsList.size )
                        Timber.i("scanTimeMs=%d", result.lastScanTimeMs)
                        Timber.i("now in  ms=%d", System.currentTimeMillis() )
                        Timber.i("time since last scan=%d", result.hoursSinceLastScanWithHarmfulApp)
                        Timber.i("hours since last scan=%f", (System.currentTimeMillis()-result.lastScanTimeMs)/3600_000.0)

                        continuation.resume( result.harmfulAppsList )
                    } ?: continuation.resumeWithException( Exception("invalid apps scan respons"))

                }

    }
}