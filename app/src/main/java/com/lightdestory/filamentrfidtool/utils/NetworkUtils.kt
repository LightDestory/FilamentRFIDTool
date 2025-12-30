package com.lightdestory.filamentrfidtool.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

// Simple connectivity helper
fun isOnline(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val network = cm?.activeNetwork ?: return false
    val caps = cm.getNetworkCapabilities(network) ?: return false
    return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

// Minimal GET helper using HttpURLConnection to avoid extra dependencies
suspend fun httpGet(url: String, connectTimeoutMs: Int = 5000, readTimeoutMs: Int = 5000): String? =
    withContext(Dispatchers.IO) {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = connectTimeoutMs
            readTimeout = readTimeoutMs
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("User-Agent", "FilamentRFIDTool")
        }
        return@withContext runCatching {
            connection.inputStream.use { input ->
                BufferedReader(InputStreamReader(input)).use { reader ->
                    reader.lineSequence().joinToString("\n")
                }
            }
        }.onFailure { connection.disconnect() }
            .getOrNull()
    }
