package com.loneoaktech.tests.safetynet.gms

import android.content.Context
import com.google.android.gms.safetynet.SafetyNet
import com.loneoaktech.tests.safetynet.BuildConfig
import com.loneoaktech.tests.safetynet.utility.base64Decode
import com.loneoaktech.tests.safetynet.utility.asUtf8String
import com.loneoaktech.tests.safetynet.utility.decodeJson
import com.squareup.moshi.Moshi
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.security.SecureRandom
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * Created by BillH on 10/31/2018
 */
class SafetyNetCaller( private val context: Context) {

    private val random = SecureRandom()

    private val moshi by lazy { Moshi.Builder().build() }

    suspend fun getAttestation(): AttestationStatement {

        return suspendCoroutine { continuation ->
            val nonce = generateNonce("Safety Net Test")

            SafetyNet.getClient( context ).attest( nonce, BuildConfig.SAFETY_NET_API_KEY)
                    .addOnSuccessListener { resp ->

                        resp.jwsResult.split(".").getOrNull(1)?.base64Decode()?.asUtf8String()?.decodeJson<AttestationStatement>(moshi)?.let { stmt ->

                            Timber.i(" nonce=%s  OK=%b", stmt.getNonce(), stmt.getNonce()?.let{ nonce.contentEquals(it) } )
                            Timber.i(" Basic=%b CTS=%b", stmt.basicIntegrity, stmt.ctsProfileMatch)

                            if ( validateStatement(stmt, nonce).not() )
                                continuation.resumeWithException( Exception("Invalid nonce"))
                            else
                                continuation.resume(stmt)
                        }?: continuation.resumeWithException( Exception("invalid response"))
                    }
                    .addOnFailureListener { t ->
                        Timber.e(t, "Attest call failure, msg=%s", t.message)
                        continuation.resumeWithException(t)
                    }
        }

    }


    private fun validateStatement( stmt: AttestationStatement, nonce: ByteArray ): Boolean {

        if ( stmt.getNonce()?.let{ nonce.contentEquals(it) } != true)
            return false

        if ( BuildConfig.APPLICATION_ID != stmt.apkPackageName )
            return false

        return true
    }

    /**
     * Creates a nonce combine
     *ing the supplied tag with the current time and some random stuff.
     */
    private fun generateNonce(tag: String): ByteArray {
        return ByteArrayOutputStream().apply {
            write( ByteArray(24).apply{ random.nextBytes(this) } )
            write( (tag + "-" + System.currentTimeMillis()).toByteArray() )
        }.toByteArray()
    }
}