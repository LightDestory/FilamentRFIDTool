package com.lightdestory.filamentrfidtool.utils

import android.content.Context
import com.lightdestory.filamentrfidtool.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val RELEASES_URL = "https://api.github.com/repos/LightDestory/FilamentRFIDTool/releases/latest"


data class UpdateInfo(
    val isUpdateAvailable: Boolean,
    val latestVersion: String,
    val releaseUrl: String?
)

suspend fun checkUpdate(context: Context): UpdateInfo? = withContext(Dispatchers.IO) {
    if (!isOnline(context)) return@withContext null
    val latest = fetchLatestFromReleases() ?: return@withContext null
    val newUpdateAvailable = BuildConfig.VERSION_NAME != latest.first
    UpdateInfo(isUpdateAvailable = newUpdateAvailable, latestVersion = latest.first, releaseUrl = latest.second)
}

private suspend fun fetchLatestFromReleases(): Pair<String, String?>? {
    val body = httpGet(RELEASES_URL) ?: return null
    return runCatching {
        val json = JSONObject(body)
        val tag = json.optString("tag_name").trim().removePrefix("v").ifEmpty { null }
        val url = json.optString("html_url").ifEmpty { null }
        tag?.let { it to url }
    }.getOrNull()
}

