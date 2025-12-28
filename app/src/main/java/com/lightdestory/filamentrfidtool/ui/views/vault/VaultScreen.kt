package com.lightdestory.filamentrfidtool.ui.views.vault

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.lightdestory.filamentrfidtool.R
import com.lightdestory.filamentrfidtool.ui.theme.FilamentRFIDToolTheme

@Composable
fun VaultScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(stringResource(id = R.string.not_yet_implemented))
    }
}

@Preview(showBackground = true)
@Composable
private fun VaultScreenPreview() {
    FilamentRFIDToolTheme { VaultScreen() }
}

