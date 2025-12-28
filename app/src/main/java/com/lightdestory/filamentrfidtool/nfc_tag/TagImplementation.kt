package com.lightdestory.filamentrfidtool.nfc_tag

import com.lightdestory.filamentrfidtool.models.FilamentSpool

interface TagImplementation {

    fun getSpoolData(): FilamentSpool
}