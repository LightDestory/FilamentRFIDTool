package com.lightdestory.filamentrfidtool.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.DialogProperties
import com.lightdestory.filamentrfidtool.R
import com.lightdestory.filamentrfidtool.utils.NfcStatus

@Composable
fun NfcStatusDialog(
    status: NfcStatus,
    onOpenSettings: () -> Unit
) {
    when (status) {
        NfcStatus.Enabled -> Unit
        NfcStatus.Disabled -> AlertDialog(
            onDismissRequest = { /* Keep dialog open until NFC is on */ },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
            title = {
                Text(
                    text = stringResource(R.string.nfc_disabled_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.nfc_disabled_message),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { onOpenSettings() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.nfc_enable_action))
                }
            }
        )

        NfcStatus.NoHardware -> AlertDialog(
            onDismissRequest = { /* Prevent dismiss; app incompatible without NFC */ },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
            title = {
                Text(
                    text = stringResource(R.string.nfc_no_hardware_title),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.nfc_no_hardware_message),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NfcStatusDialogPreview() {
    NfcStatusDialog(
        status = NfcStatus.NoHardware,
        onOpenSettings = {}
    )
}
