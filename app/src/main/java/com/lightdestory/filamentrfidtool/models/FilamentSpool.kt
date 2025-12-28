package com.lightdestory.filamentrfidtool.models

data class FilamentSpool(
    val manufacturer: String = "Bambu Lab",
    val material: String = "PLA",
    val colorName: String = "RedBlue",
    val colorHex: String = "#FF0000-#0000FF",
    val weightGrams: Int = 1000,
    val diameterMm: Double = 1.75,
    val productionDate: String = "1980/01/01",
    val uid: String = "CAPYBARA"
)
