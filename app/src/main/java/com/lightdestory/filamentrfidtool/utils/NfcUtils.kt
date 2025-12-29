package com.lightdestory.filamentrfidtool.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.provider.Settings

enum class NfcStatus {
    Enabled,
    Disabled,
    NoHardware
}

fun resolveNfcStatus(context: Context): NfcStatus {
    val adapter = runCatching { NfcAdapter.getDefaultAdapter(context) }.getOrNull()
    return when {
        adapter == null -> NfcStatus.NoHardware
        adapter.isEnabled -> NfcStatus.Enabled
        else -> NfcStatus.Disabled
    }
}

fun openNfcSettings(context: Context) {
    val settingsIntent =
        Intent(Settings.ACTION_NFC_SETTINGS).takeIf {
            context.packageManager.resolveActivity(it, 0) != null
        } ?: Intent(Settings.ACTION_WIRELESS_SETTINGS)

    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(settingsIntent)
}

fun registerNfcStateReceiver(
    context: Context,
    onStatusChanged: (NfcStatus) -> Unit
): BroadcastReceiver? {
    if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) return null
    val filter = IntentFilter(NfcAdapter.ACTION_ADAPTER_STATE_CHANGED)
    val receiver =
        object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (ctx == null || intent == null) return
                val state = intent.getIntExtra(NfcAdapter.EXTRA_ADAPTER_STATE, NfcAdapter.STATE_OFF)
                val status =
                    when (state) {
                        NfcAdapter.STATE_ON -> NfcStatus.Enabled
                        NfcAdapter.STATE_OFF -> NfcStatus.Disabled
                        else -> resolveNfcStatus(ctx)
                    }
                onStatusChanged(status)
            }
        }
    return runCatching { context.registerReceiver(receiver, filter) }.getOrNull()?.let { receiver }
}

