package com.loneoaktech.tests.safetynet.gms

import android.content.Context
import android.util.Base64
import com.google.android.gms.safetynet.SafetyNet
import com.loneoaktech.tests.safetynet.BuildConfig
import com.squareup.moshi.Moshi
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.security.SecureRandom

/**
 * Created by BillH on 10/31/2018
 */
class SafetyNetCaller( private val context: Context) {

    private val random = SecureRandom()

    private val moshi by lazy { Moshi.Builder().build() }

    fun getAttestation() {

        val nonce = generateNonce("Safety Net Test")

        SafetyNet.getClient( context ).attest( nonce, BuildConfig.SAFETY_NET_API_KEY)
                .addOnSuccessListener { resp ->


                    Timber.i("Success, resp=%s", resp.jwsResult )

                    Timber.i( "Num parts=%s", resp.jwsResult.split(".").map { it.length } )

                    resp.jwsResult.split(".").map { part ->

                        val decoded = Base64.decode(part, Base64.DEFAULT ).toString( Charset.forName("UTF-8") )
                        Timber.i(" part=%s", decoded )
                    }
                }
                .addOnFailureListener { t ->

                    Timber.e(t, "Attest call failure, msg=%s", t.message)
                }
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