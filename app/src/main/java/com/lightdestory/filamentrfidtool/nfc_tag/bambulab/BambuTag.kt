package com.lightdestory.filamentrfidtool.nfc_tag.bambulab

import android.util.Log
import com.lightdestory.filamentrfidtool.models.FilamentSpool
import com.lightdestory.filamentrfidtool.nfc_tag.TagImplementation
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.HKDFBytesGenerator
import org.bouncycastle.crypto.params.HKDFParameters


object BambuTag : TagImplementation {

    private val MASTER_KEY = byteArrayOf(
        0x9a.toByte(), 0x75.toByte(), 0x9c.toByte(), 0xf2.toByte(),
        0xc4.toByte(), 0xf7.toByte(), 0xca.toByte(), 0xff.toByte(),
        0x22.toByte(), 0x2c.toByte(), 0xb9.toByte(), 0x76.toByte(),
        0x9b.toByte(), 0x41.toByte(), 0xbc.toByte(), 0x96.toByte()
    )

    /**
     * Derives sector keys from the given UID using HKDF with SHA-256.
     *
     * @param uid The UID of the RFID tag.
     * @param sectorCount The number of sectors to derive keys for.
     * @return A list of derived keys, one for each sector.
     */
    fun deriveKeys(uid: ByteArray, sectorCount: Int): List<ByteArray> {
        // Fixed context string for HKDF derivation
        val context = "RFID-A\u0000".toByteArray(Charsets.UTF_8)
        // Initialize the HKDFBytesGenerator with a SHA-256 digest
        val hkdf = HKDFBytesGenerator(SHA256Digest())
        val keys = mutableListOf<ByteArray>()
        // Calculate the total key length required (6 bytes per sector)
        val totalKeyLength = sectorCount * 6
        val derivedBuffer = ByteArray(totalKeyLength)
        // Initialize the HKDF with the UID (salt), master key, and context
        hkdf.init(HKDFParameters(uid, MASTER_KEY, context))
        // Generate the derived keys and fill the buffer
        hkdf.generateBytes(derivedBuffer, 0, totalKeyLength)
        // Split the derived buffer into individual keys (6 bytes each) for each sector
        for (i in 0 until sectorCount) {
            val start = i * 6
            val key = derivedBuffer.copyOfRange(start, start + 6) // Extract a 6-byte key
            keys.add(key) // Add the key to the list
            Log.d(
                "BambuTag",
                "Derived Key A for sector $i: ${key.joinToString("") { "%02X".format(it) }}"
            )
        }
        return keys
    }

    override fun getSpoolData(): FilamentSpool {
        return FilamentSpool(
            manufacturer = "Bambu Lab",
            material = "PLA Pro",
            colorName = "White",
            colorHex = "#FFFFFF",
            weightGrams = 1000,
            diameterMm = 1.75,
            uid = "BAMBUTAG001",
            productionDate = "2024/01/01"
        )
    }
}