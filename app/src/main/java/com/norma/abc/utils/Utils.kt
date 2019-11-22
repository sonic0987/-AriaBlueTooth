package com.norma.abc.utils

import android.util.Log
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

//[convert Kotlin]
class Utils {
    companion object{
        private val aesKey = "ABCDEF0123456789"
        fun enc(msg:ByteArray): String? {
            val skeySpec = SecretKeySpec(aesKey.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
            val encrypted = cipher.doFinal(msg)
            return toHex(encrypted)
        }
        fun dec(msg:String):String?{
            val skeySpec = SecretKeySpec(aesKey.toByteArray(), "AES")
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec)
            val original = cipher.doFinal(toByteArray(msg))
            return String(original)
        }
        fun toHex(ba: ByteArray?): String? {
            if (ba == null || ba.isEmpty()) { return null }

            val sb = StringBuffer(ba.size * 2)
            var hexNumber: String

            for (x in ba.indices) {
                hexNumber = "0" + Integer.toHexString(0xff and ba[x].toInt())
                sb.append(hexNumber.substring(hexNumber.length - 2))
            }
            return sb.toString()

        }
        fun toHexSingle(ba: Byte): String? {
            val hexNumber = "0" + Integer.toHexString(0xff and ba.toInt())
            return hexNumber.substring(hexNumber.length - 2)

        }
        fun toByteArray(hex: String?): ByteArray? {
            if (hex == null || hex.isEmpty()) { return null }

            val ba = ByteArray(hex.length / 2)
            for (i in ba.indices)
                ba[i] = Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16).toByte()

            return ba
        }
        fun toByte(hex: String):Byte{
            Log.e("hex",hex)
            return Integer.parseInt(hex, 16).toByte()
        }
    }
}
