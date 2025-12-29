package com.lightdestory.filamentrfidtool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import com.lightdestory.filamentrfidtool.R
import com.lightdestory.filamentrfidtool.models.FilamentSpool

@Composable
fun FilamentDetailsCard(
    spoolData: FilamentSpool,
    modifier: Modifier = Modifier
) {
    val parsedColors = Array<Color?>(2) { null }
    if (spoolData.colorHex.contains('-')) {
        parsedColors[0] =
            runCatching { Color(spoolData.colorHex.split('-')[0].toColorInt()) }.getOrDefault(Color.White)
        parsedColors[1] =
            runCatching { Color(spoolData.colorHex.split('-')[1].toColorInt()) }.getOrNull()
    } else {
        parsedColors[0] = runCatching { Color(spoolData.colorHex.toColorInt()) }.getOrDefault(Color.White)
    }
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.scanner_filament_details),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            DetailItemCard(
                label = stringResource(R.string.scanner_filament_uid),
                value = spoolData.uid
            )
            DetailItemCard(
                label = stringResource(R.string.scanner_filament_manufacturer),
                value = spoolData.manufacturer
            )
            DetailItemCard(
                label = stringResource(R.string.scanner_filament_material),
                value = spoolData.material
            )
            DetailItemCard(
                label = stringResource(R.string.scanner_filament_color_name),
                value = "${spoolData.colorName} (${spoolData.colorHex})"
            )
            DetailItemCard(
                label = stringResource(id = R.string.scanner_filament_weight),
                value = "${spoolData.weightGrams} g"
            )
            DetailItemCard(
                label = stringResource(R.string.scanner_filament_diameter),
                value = "${spoolData.diameterMm} mm"
            )
            DetailItemCard(
                label = stringResource(R.string.scanner_filament_production_date),
                value = spoolData.productionDate
            )
            ColorItemCard(colors = parsedColors)
        }
    }
}

@Composable
private fun DetailItemCard(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(text = value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun ColorItemCard(colors: Array<Color?>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.scanner_filament_color),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = if (colors[1] != null) {
                    Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.linearGradient(listOf(colors[0]!!, colors[1]!!)),
                            shape = CircleShape
                        )
                } else {
                    Modifier
                        .size(48.dp)
                        .background(color = colors[0]!!, shape = CircleShape)
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FilamentDetailsCardPreview() {
    FilamentDetailsCard(
        FilamentSpool()
    )
}
