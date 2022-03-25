package com.fastaccess.provider.rest

import android.content.Context
import com.google.gson.reflect.TypeToken
import okhttp3.Dns
import java.lang.reflect.Type
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.UnknownHostException

inline fun <reified T> genericType(): Type = object : TypeToken<T>() {}.type

class DnsProvider : Dns {
    private val builtInHosts = mutableMapOf<String, InetAddress>()

    companion object {
        //        private val hostUrl = "https://raw.fastgit.org/521xueweihan/GitHub520/main/hosts.json"
        val instance: DnsProvider = DnsProvider()
    }

    private fun tcpPing(address: InetAddress, port: Int): Long {
        val startTime = System.currentTimeMillis()
        val socket = Socket()
        socket.use { s ->
            s.connect(InetSocketAddress(address, port), 3000)
        }
        return System.currentTimeMillis() - startTime
    }

    private fun isValidHost(host: String?): Boolean {
        if (host == null) {
            return false
        }
        if (host.length > 253) {
            return false
        }
        var labelLength = 0
        var i = 0
        val n = host.length
        while (i < n) {
            val ch = host[i]
            if (ch == '.') {
                if (labelLength < 1 || labelLength > 63) {
                    return false
                }
                labelLength = 0
            } else {
                labelLength++
            }
            if ((ch < 'a' || ch > 'z') && (ch < '0' || ch > '9') && ch != '-' && ch != '.') {
                return false
            }
            i++
        }
        return !(labelLength < 1 || labelLength > 63)
    }

    fun toInetAddress(host: String?, ip: String?): InetAddress? {
        if (!isValidHost(host)) {
            return null
        }
        if (ip == null) {
            return null
        }
        var bytes: ByteArray? = parseV4(ip)
        if (bytes == null) {
            bytes = parseV6(ip)
        }
        return if (bytes == null) {
            null
        } else try {
            InetAddress.getByAddress(host, bytes)
        } catch (e: UnknownHostException) {
            null
        }
    }

    private fun parseV4(s: String): ByteArray? {
        var numDigits: Int
        val values = ByteArray(4)
        var currentValue: Int
        val length = s.length
        var currentOctet = 0
        currentValue = 0
        numDigits = 0
        for (i in 0 until length) {
            val c = s[i]
            if (c in '0'..'9') {
                /* Can't have more than 3 digits per octet. */
                if (numDigits == 3) return null
                /* Octets shouldn't start with 0, unless they are 0. */if (numDigits > 0 && currentValue == 0) return null
                numDigits++
                currentValue *= 10
                currentValue += c - '0'
                /* 255 is the maximum value for an octet. */if (currentValue > 255) return null
            } else if (c == '.') {
                /* Can't have more than 3 dots. */
                if (currentOctet == 3) return null
                /* Two consecutive dots are bad. */if (numDigits == 0) return null
                values[currentOctet++] = currentValue.toByte()
                currentValue = 0
                numDigits = 0
            } else return null
        }
        /* Must have 4 octets. */if (currentOctet != 3) return null
        /* The fourth octet can't be empty. */if (numDigits == 0) return null
        values[currentOctet] = currentValue.toByte()
        return values
    }

    private fun parseV6(s: String): ByteArray? {
        var range = -1
        val data = ByteArray(16)
        val tokens = s.split(":").dropLastWhile { it.isEmpty() }.toTypedArray()
        var first = 0
        var last = tokens.size - 1
        if (tokens[0].isEmpty()) {
            if (last - first > 0 && tokens[1].isEmpty()) first++ else return null
        }
        if (tokens[last].isEmpty()) {
            if (last - first > 0 && tokens[last - 1].isEmpty()) last-- else return null
        }
        if (last - first + 1 > 8) return null
        var i: Int
        i = first
        var j = 0
        while (i <= last) {
            if (tokens[i].isEmpty()) {
                if (range >= 0) return null
                range = j
                i++
                continue
            }
            if (tokens[i].indexOf('.') >= 0) {
                if (i < last) return null
                if (i > 6) return null
                val v4adder = parseV4(tokens[i]) ?: return null
                for (k in 0..3) data[j++] = v4adder[k]
                break
            }
            try {
                for (k in 0 until tokens[i].length) {
                    val c = tokens[i][k]
                    if (Character.digit(c, 16) < 0) return null
                }
                val x = tokens[i].toInt(16)
                if (x > 0xFFFF || x < 0) return null
                data[j++] = (x ushr 8).toByte()
                data[j++] = (x and 0xFF).toByte()
            } catch (e: NumberFormatException) {
                return null
            }
            i++
        }
        if (j < 16 && range < 0) return null
        if (range >= 0) {
            val empty = 16 - j
            System.arraycopy(data, range, data, range + empty, j - range)
            i = range
            while (i < range + empty) {
                data[i] = 0
                i++
            }
        }
        return data
    }

    fun init(context: Context) {
//        val f: Reader = InputStreamReader(context.assets.open("hosts.json"))
//        val gson = Gson()
//        val type = genericType<Array<Array<String>>>()
//        val hosts = gson.fromJson<Array<Array<String>>>(f, type)
//        f.close()
//        builtInHosts.clear()
//        hosts.filter { it.size >= 2 }.forEach { host ->
//            val adder = toInetAddress(host[1], host[0])
//            adder?.let { address ->
//                builtInHosts[host[1]] = address
//            }
//        }
    }

    override fun lookup(hostname: String): List<InetAddress> {
        val inetAddress = builtInHosts[hostname]
        if (inetAddress != null) {
            return listOf(inetAddress)
        }
        return InetAddress.getAllByName(hostname).toList()
    }
}