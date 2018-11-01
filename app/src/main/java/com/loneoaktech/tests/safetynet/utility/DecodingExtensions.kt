package com.loneoaktech.tests.safetynet.utility

import android.util.Base64
import com.squareup.moshi.Moshi
import java.nio.charset.Charset

/**
 * Created by BillH on 11/1/2018
 */


/**
 * Decode a base 64 encoded string to a ByteArray
 */
fun String.base64Decode(): ByteArray =
        this.let{ Base64.decode(it, Base64.DEFAULT)}


/**
 * Convert a ByteArray to a string using UTF-8 encoding
 */
fun ByteArray.asUtf8String(): String =
        this.toString( Charset.forName("UTF-8"))


/**
 * Shortcut to use Moshi to decode a json string.
 * Constructs the appropriate adapter.
 */
inline fun <reified T> String.decodeJson(moshi: Moshi): T? {
    return moshi.adapter(T::class.java).fromJson(this)
}

/**
 * Runs the specified test on the subject, if true, returns the subject,
 * if false, return null
 */
fun <T> T.nullIfFalse(predicate: (T) -> Boolean): T? =
        if (predicate(this)) this else null

fun <T> T.nullIfTrue(predicate: (T) -> Boolean): T? =
        if (predicate(this).not()) this else null


