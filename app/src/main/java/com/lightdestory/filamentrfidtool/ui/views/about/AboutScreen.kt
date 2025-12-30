package com.lightdestory.filamentrfidtool.ui.views.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.lightdestory.filamentrfidtool.BuildConfig
import com.lightdestory.filamentrfidtool.R
import com.lightdestory.filamentrfidtool.ui.components.InfoCard
import com.lightdestory.filamentrfidtool.ui.components.OpenSourceLicensesDialog
import com.lightdestory.filamentrfidtool.ui.theme.FilamentRFIDToolTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AboutScreen(
    appName: String = stringResource(id = R.string.app_name),
    licenses: List<OpenSourceLicense> = defaultLicenses()
) {
    val uriHandler = LocalUriHandler.current
    val showLicenses = remember { mutableStateOf(false) }
    val showLicensesState by showLicenses
    val githubUrl = stringResource(R.string.about_github_url)
    val kofiUrl = stringResource(R.string.about_kofi_url)
    val rfidUrl = stringResource(R.string.about_rfid_url)
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    horizontal =
                        dimensionResource(id = R.dimen.spacing_large),
                    vertical = dimensionResource(id = R.dimen.spacing_none)
                )
                .verticalScroll(rememberScrollState()),
        verticalArrangement =
            Arrangement.spacedBy(
                dimensionResource(id = R.dimen.spacing_xl),
                Alignment.Top
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_small))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = null,
                modifier = Modifier.size(dimensionResource(id = R.dimen.app_logo_size))
            )
            Text(
                text = appName,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text =
                    stringResource(R.string.about_version_label) +
                            ": " +
                            BuildConfig.VERSION_NAME,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }

        FlowRow(
            maxItemsInEachRow = 1,
            horizontalArrangement =
                Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_large)),
            verticalArrangement =
                Arrangement.spacedBy(dimensionResource(id = R.dimen.spacing_large)),
            modifier = Modifier.fillMaxWidth()
                .padding(dimensionResource(R.dimen.spacing_none), dimensionResource(R.dimen.spacing_none), dimensionResource(R.dimen.spacing_none), dimensionResource(id = R.dimen.spacing_xl))
        ) {
            InfoCard(
                title = stringResource(R.string.about_update_check),
                description = stringResource(R.string.about_update_check_description),
                iconRes = R.drawable.ic_launcher_foreground,
                onClick = {  },
                onClickIcon = Icons.Default.Update,
                modifier = Modifier.fillMaxWidth()
            )
            InfoCard(
                title = stringResource(R.string.about_github_label),
                description = stringResource(R.string.about_github_description),
                iconRes = R.drawable.github_icon,
                onClick = { uriHandler.openUri(githubUrl) },
                onClickIcon = Icons.AutoMirrored.Default.OpenInNew,
                modifier = Modifier.fillMaxWidth()
            )
            InfoCard(
                title = stringResource(R.string.about_rfid_label),
                description = stringResource(R.string.about_rfid_description),
                iconRes = R.drawable.rfid_research_icon,
                onClick = { uriHandler.openUri(rfidUrl) },
                onClickIcon = Icons.AutoMirrored.Default.OpenInNew,
                modifier = Modifier.fillMaxWidth()
            )
            InfoCard(
                title = stringResource(R.string.about_kofi_label),
                description = stringResource(R.string.about_kofi_description),
                iconRes = R.drawable.kofi_icon,
                onClick = { uriHandler.openUri(kofiUrl) },
                onClickIcon = Icons.AutoMirrored.Default.OpenInNew,
                modifier = Modifier.fillMaxWidth()
            )
            InfoCard(
                title = stringResource(R.string.about_licenses_label),
                description = stringResource(R.string.about_licenses_description),
                iconRes = R.drawable.license_icon,
                onClick = { showLicenses.value = true },
                onClickIcon = Icons.Default.ChevronRight,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (showLicensesState) {
        OpenSourceLicensesDialog(
            licenses = licenses,
            onDismiss = { showLicenses.value = false }
        )
    }
}

data class OpenSourceLicense(
    val library: String,
    val license: String,
    val projectUrl: String,
    val licenseUrl: String? = null
)

private fun defaultLicenses(): List<OpenSourceLicense> =
    listOf(
        OpenSourceLicense(
            library = "Jetpack Compose",
            license = "Apache-2.0",
            projectUrl = "https://developer.android.com/jetpack/compose",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0"
        ),
        OpenSourceLicense(
            library = "AndroidX Navigation",
            license = "Apache-2.0",
            projectUrl =
                "https://developer.android.com/jetpack/androidx/releases/navigation",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0"
        ),
        OpenSourceLicense(
            library = "Material3",
            license = "Apache-2.0",
            projectUrl =
                "https://github.com/material-components/material-components-android",
            licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0"
        ),
        OpenSourceLicense(
            library = "Bouncy Castle Provider",
            license = "MIT",
            projectUrl = "https://www.bouncycastle.org/download/bouncy-castle-java/",
            licenseUrl = "https://www.bouncycastle.org/about/license/"
        )
    )

@Preview(showBackground = true)
@Composable
private fun AboutScreenPreview() {
    FilamentRFIDToolTheme { AboutScreen() }
}
