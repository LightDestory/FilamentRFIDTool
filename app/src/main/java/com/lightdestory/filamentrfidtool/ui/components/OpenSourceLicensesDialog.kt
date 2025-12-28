package com.lightdestory.filamentrfidtool.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.lightdestory.filamentrfidtool.R
import com.lightdestory.filamentrfidtool.ui.views.about.OpenSourceLicense

@Composable
fun OpenSourceLicensesDialog(
    licenses: List<OpenSourceLicense>,
    onDismiss: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.about_licenses_label),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_large))) {
                items(licenses) { license ->
                    Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_micro))) {
                        Text(text = license.library, fontWeight = FontWeight.SemiBold)
                        Text(text = stringResource(R.string.about_license_format, license.license))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(
                                dimensionResource(id = R.dimen.spacing_small),
                                Alignment.CenterHorizontally
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TextButton(onClick = { uriHandler.openUri(license.projectUrl) }) {
                                Text(text = stringResource(R.string.about_license_open_project))
                            }
                            license.licenseUrl?.let { url ->
                                TextButton(onClick = { uriHandler.openUri(url) }) {
                                    Text(text = stringResource(R.string.about_license_view_license))
                                }
                            }
                        }
                    }
                    HorizontalDivider()
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.about_licenses_close))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun OpenSourceLicensesDialogPreview() {
    val sampleLicenses = listOf(
        OpenSourceLicense(
            library = "Compose",
            license = "Apache-2.0",
            projectUrl = "https://developer.android.com/jetpack/compose",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0"
        ),
        OpenSourceLicense(
            library = "Material3",
            license = "Apache-2.0",
            projectUrl = "https://github.com/material-components/material-components-android",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0"
        )
    )
    val showDialog = remember { mutableStateOf(true) }
    if (showDialog.value) {
        OpenSourceLicensesDialog(licenses = sampleLicenses, onDismiss = { showDialog.value = false })
    }
}
