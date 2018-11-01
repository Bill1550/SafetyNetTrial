package com.loneoaktech.tests.safetynet.gms

import android.util.Base64

/**
 * Created by BillH on 10/31/2018
 */
//import com.google.api.client.json.webtoken.JsonWebSignature
//import com.google.api.client.util.Base64
//import com.google.api.client.util.Key
//import java.util.*
//
///**
// * A statement returned by the Attestation API.
// */
data class AttestationStatement(
    /**
     * Embedded nonce sent as part of the request.
     */
    val nonce: String? = null,

    /**
     * Timestamp of the request.
     */
    val timestampMs: Long = 0,

    /**
     * Package name of the APK that submitted this request.
     */
    val apkPackageName: String? = null,

    /**
     * Digest of certificate of the APK that submitted this request.
     */
    val apkCertificateDigestSha256: List<String>? = null,

    /**
     * Digest of the APK that submitted this request.
     */
    val apkDigestSha256: String? = null,

    /**
     * The device passed CTS and matches a known profile.
     */
    val ctsProfileMatch: Boolean = false,


    /**
     * The device has passed a basic integrity test, but the CTS profile could not be verified.
     */
    val basicIntegrity: Boolean = false,

    /**
     * Message key for how to fix issues.
     */
    val advice: String? = null
)


{
    fun getNonce(): ByteArray? {
        return nonce?.let { Base64.decode(it, Base64.DEFAULT) }
    }

    fun getApkDigestSha256(): ByteArray {
        return Base64.decode(apkDigestSha256, Base64.DEFAULT)
    }


    fun hasBasicIntegrity(): Boolean {
        return basicIntegrity
    }
}