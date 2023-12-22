package com.kotlinkhaos.classes.utils

import java.io.InputStream
import java.security.MessageDigest

/**
 * Calculates the SHA-256 hash of content read from a given InputStream
 * @param inputStream The InputStream to read the content from.
 * @return The hexadecimal string representing the SHA-256 hash of the content.
 */
fun calculateSha256Hash(inputStream: InputStream): String {
    return inputStream.use { stream ->
        val buffer = ByteArray(1024)
        val digest = MessageDigest.getInstance("SHA-256")
        var bytesRead: Int
        while (stream.read(buffer).also { bytesRead = it } != -1) {
            digest.update(buffer, 0, bytesRead)
        }
        bytesToHex(digest.digest())
    }
}

/**
 * Converts a byte array to a hexadecimal string.
 * This is a helper function used by calculateSha256Hash to convert the hash byte array to a hex string.
 *
 * @param hash The byte array to be converted into a hexadecimal string.
 * @return The hexadecimal string representation of the byte array.
 */
private fun bytesToHex(hash: ByteArray): String {
    val hexString = StringBuilder(2 * hash.size)
    for (byte in hash) {
        val hex = Integer.toHexString(0xff and byte.toInt())
        if (hex.length == 1) {
            hexString.append('0')
        }
        hexString.append(hex)
    }
    return hexString.toString()
}
