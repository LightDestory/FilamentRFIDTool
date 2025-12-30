package com.lightdestory.filamentrfidtool.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.lightdestory.filamentrfidtool.R
import com.lightdestory.filamentrfidtool.utils.UpdateInfo

@Composable
fun UpdateAvailableDialog(
    updateInfo: UpdateInfo,
    onDismiss: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    val releaseUrl = updateInfo.releaseUrl

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(
                    id = R.string.about_update_available
                ),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = stringResource(
                    id = R.string.about_update_description,
                    updateInfo.latestVersion
                ),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    releaseUrl?.let(uriHandler::openUri)
                    onDismiss()
                },
                enabled = releaseUrl != null,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.about_update_open_release))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.about_licenses_close))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun UpdateAvailableDialogPreview() {
    UpdateAvailableDialog(
        updateInfo = UpdateInfo(isUpdateAvailable = true, latestVersion = "0.0.0", releaseUrl = "https://example.com"),
        onDismiss = {}
    )
}
