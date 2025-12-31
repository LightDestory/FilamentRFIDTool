package com.lightdestory.filamentrfidtool.nfc_tag

import android.nfc.Tag
import com.lightdestory.filamentrfidtool.models.FilamentSpool

interface TagImplementation {

    fun getSpoolData(tag: Tag): FilamentSpool?
}