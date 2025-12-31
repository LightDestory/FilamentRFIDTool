package com.lightdestory.filamentrfidtool.nfc_tag.creality

import android.nfc.Tag
import com.lightdestory.filamentrfidtool.models.FilamentSpool
import com.lightdestory.filamentrfidtool.nfc_tag.TagImplementation

object CrealityTag: TagImplementation {

    override fun getSpoolData(tag: Tag): FilamentSpool? {
        TODO("Not yet implemented")
    }
}