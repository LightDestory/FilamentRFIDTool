package com.lightdestory.filamentrfidtool.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.nfc.tech.NfcA
import android.provider.Settings
import android.util.Log

enum class NfcStatus {
    Enabled,
    Disabled,
    NoHardware
}

enum class TagType {
    MifareClassic1K,
    Ntag213,
    Ntag215,
    Ntag216,
    Unsupported
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

fun isSupportedTag(tag: Tag?): TagType {
    if (tag == null) return TagType.Unsupported
    val techs = tag.techList
    val hasMifareClassic = techs.contains(MifareClassic::class.java.name)
    if (hasMifareClassic) {
        val mc = MifareClassic.get(tag)
        return if (mc?.type == MifareClassic.TYPE_CLASSIC && mc.size == MifareClassic.SIZE_1K) TagType.MifareClassic1K else TagType.Unsupported
    }
    val hasNfcA = techs.contains(NfcA::class.java.name)
    if (!hasNfcA) return TagType.Unsupported
    val ndef = Ndef.get(tag)
    // NTAG213/215/216 use NfcA with NDEF; check maxSize as a practical filter
    val ndefSize = ndef?.maxSize ?: NdefFormatable.get(tag)?.let { 0 } ?: -1
    return when (ndefSize) {
        144 -> TagType.Ntag213
        504 -> TagType.Ntag215
        888 -> TagType.Ntag216
        else -> TagType.Unsupported
    }
}
