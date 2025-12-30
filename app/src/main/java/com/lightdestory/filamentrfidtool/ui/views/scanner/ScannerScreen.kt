package com.lightdestory.filamentrfidtool.ui.views.scanner

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.lightdestory.filamentrfidtool.R
import com.lightdestory.filamentrfidtool.models.FilamentSpool
import com.lightdestory.filamentrfidtool.ui.components.FilamentDetailsCard
import com.lightdestory.filamentrfidtool.ui.components.ScanDialog
import com.lightdestory.filamentrfidtool.utils.TagType.MifareClassic1K
import com.lightdestory.filamentrfidtool.utils.TagType.Ntag213
import com.lightdestory.filamentrfidtool.utils.TagType.Ntag215
import com.lightdestory.filamentrfidtool.utils.TagType.Ntag216
import com.lightdestory.filamentrfidtool.utils.TagType.Unsupported
import com.lightdestory.filamentrfidtool.utils.isSupportedTag

@Composable
fun ScannerScreen(latestNfcIntent: State<Intent?>) {
    val context = LocalContext.current
    val activity = context as? Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    val nfcAdapter = remember { activity?.let { NfcAdapter.getDefaultAdapter(it) } }
    val isScanning = remember { mutableStateOf(false) }
    val lastProcessedIntent = remember { mutableStateOf<Intent?>(null) }
    val currentIsScanning by rememberUpdatedState(isScanning.value)

    DisposableEffect(lifecycleOwner, nfcAdapter) {
        if (nfcAdapter == null || activity == null) return@DisposableEffect onDispose { }
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> if (currentIsScanning) enableDispatch(
                    nfcAdapter,
                    activity
                )

                Lifecycle.Event.ON_PAUSE -> disableDispatch(nfcAdapter, activity)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            disableDispatch(nfcAdapter, activity)
        }
    }

    LaunchedEffect(latestNfcIntent.value, isScanning.value) {
        if (!isScanning.value) return@LaunchedEffect
        if (activity == null || nfcAdapter == null) return@LaunchedEffect
        val intent = latestNfcIntent.value
        if (intent == null || intent == lastProcessedIntent.value) return@LaunchedEffect
        if (intent.action == NfcAdapter.ACTION_TAG_DISCOVERED || intent.action == NfcAdapter.ACTION_TECH_DISCOVERED) {
            handleTagIntent(intent, context, onDone = { isScanning.value = false })
            lastProcessedIntent.value = intent
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = dimensionResource(id = R.dimen.spacing_xl),
                    vertical = dimensionResource(id = R.dimen.spacing_xl)
                ),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FilamentDetailsCard(
                    FilamentSpool()
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (activity == null || nfcAdapter == null) return@Button
                    isScanning.value = true
                    enableDispatch(nfcAdapter, activity)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.info_card_min_height)),
                shape = ButtonDefaults.shape,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Nfc,
                    contentDescription = null,
                    modifier = Modifier.size(dimensionResource(id = R.dimen.button_icon_size)),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.scanner_scan_button),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }

    if (isScanning.value) {
        ScanDialog(
            visible = true,
            onAbort = {
                isScanning.value = false
                if (nfcAdapter != null && activity != null) disableDispatch(nfcAdapter, activity)
            }
        )
    }
}

private fun enableDispatch(adapter: NfcAdapter, activity: Activity) {
    val intent = Intent(
        activity.applicationContext,
        activity.javaClass
    ).apply { addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP) }
    val pendingIntent = android.app.PendingIntent.getActivity(
        activity,
        0,
        intent,
        android.app.PendingIntent.FLAG_MUTABLE or android.app.PendingIntent.FLAG_UPDATE_CURRENT
    )
    val techList = arrayOf(arrayOf(MifareClassic::class.java.name))
    adapter.enableForegroundDispatch(activity, pendingIntent, null, techList)
}

private fun disableDispatch(adapter: NfcAdapter, activity: Activity) {
    runCatching { adapter.disableForegroundDispatch(activity) }
}

private fun handleTagIntent(intent: Intent, context: android.content.Context, onDone: () -> Unit) {
    val tag: Tag? = intent.getParcelableTag()
    val tagType = isSupportedTag(tag)
    when (tagType) {
        MifareClassic1K -> {
            handleMifareClassic(context, onDone)
            return
        }
        Ntag213, Ntag215, Ntag216-> {
            Toast.makeText(
                context,
                context.getString(R.string.not_yet_implemented), Toast.LENGTH_SHORT
            ).show()
            onDone()
            return
        }
        Unsupported -> {
            Toast.makeText(
                context,
                context.getString(R.string.no_compatible_tag_detected), Toast.LENGTH_SHORT
            ).show()
            Log.w("ScannerScreen", "Unsupported tag detected: ${tag?.techList?.joinToString()}")
            onDone()
            return
        }
    }
}

private fun handleMifareClassic(context: android.content.Context, onDone: () -> Unit) {
    // Show a simple dialog indicating this feature is not yet implemented.
    Log.i("ScannerScreen", "Mifare Classic tag detected. Showing not-implemented dialog.")
    try {
        AlertDialog.Builder(context)
            .setTitle(null)
            .setMessage(context.getString(R.string.not_yet_implemented))
            .setPositiveButton(android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                onDone()
            }
            .setOnCancelListener {
                onDone()
            }
            .show()
    } catch (e: Exception) {
        // fallback: toast + call onDone
        Log.w("ScannerScreen", "Failed to show dialog, falling back to Toast", e)
        Toast.makeText(context, context.getString(R.string.not_yet_implemented), Toast.LENGTH_SHORT).show()
        onDone()
    }
}

private fun Intent.getParcelableTag(): Tag? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(NfcAdapter.EXTRA_TAG, Tag::class.java)
    } else {
        @Suppress("DEPRECATION")
        getParcelableExtra(NfcAdapter.EXTRA_TAG)
    }
}

@Preview(showBackground = true)
@Composable
private fun ScannerScreenPreview() {
    val previewIntent = remember { mutableStateOf<Intent?>(null) }
    ScannerScreen(latestNfcIntent = previewIntent)
}
