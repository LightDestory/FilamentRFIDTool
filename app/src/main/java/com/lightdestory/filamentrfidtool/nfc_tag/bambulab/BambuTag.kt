package com.lightdestory.filamentrfidtool.nfc_tag.bambulab

import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.util.Log
import com.lightdestory.filamentrfidtool.models.FilamentSpool
import com.lightdestory.filamentrfidtool.nfc_tag.TagImplementation
import org.bouncycastle.crypto.digests.SHA256Digest
import org.bouncycastle.crypto.generators.HKDFBytesGenerator
import org.bouncycastle.crypto.params.HKDFParameters
import java.nio.ByteBuffer
import java.nio.ByteOrder


object BambuTag : TagImplementation {

    private val MASTER_KEY = byteArrayOf(
        0x9a.toByte(), 0x75.toByte(), 0x9c.toByte(), 0xf2.toByte(),
        0xc4.toByte(), 0xf7.toByte(), 0xca.toByte(), 0xff.toByte(),
        0x22.toByte(), 0x2c.toByte(), 0xb9.toByte(), 0x76.toByte(),
        0x9b.toByte(), 0x41.toByte(), 0xbc.toByte(), 0x96.toByte()
    )

    /**
     * Derives sector A keys from the given UID using HKDF with SHA-256.
     *
     * @param uid The UID of the RFID tag.
     * @param sectorCount The number of sectors to derive keys for.
     * @return A list of derived keys, one for each sector.
     */
    private fun deriveAKeys(uid: ByteArray, sectorCount: Int): List<ByteArray> {
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

    private fun parseProductionDate(rawBytes: ByteArray): String {
        val raw = rawBytes.toString(Charsets.UTF_8).trim { it <= ' ' }
        if (raw.isBlank()) return "Unknown"
        val parts = raw.split("_").filter { it.isNotEmpty() }
        if (parts.size < 3) return "Unknown"
        val month = parts[1].padStart(2, '0').takeLast(2)
        val day = parts[2].padStart(2, '0').takeLast(2)
        return "20${parts[0]}/$month/$day"
    }

    private fun ensureSectorAuth(
        mifare: MifareClassic,
        blockIndex: Int,
        keys: List<ByteArray>,
        authenticatedSectors: MutableSet<Int>
    ): Boolean {
        val sectorIndex = mifare.blockToSector(blockIndex)
        if (sectorIndex in authenticatedSectors) return true
        val key = keys.getOrNull(sectorIndex) ?: return false
        val ok = mifare.authenticateSectorWithKeyA(sectorIndex, key)
        if (ok) authenticatedSectors += sectorIndex
        return ok
    }

    private fun readBlock(
        mifare: MifareClassic,
        blockIndex: Int,
        keys: List<ByteArray>,
        authenticatedSectors: MutableSet<Int>
    ): ByteArray? {
        return if (ensureSectorAuth(mifare, blockIndex, keys, authenticatedSectors)) {
            mifare.readBlock(blockIndex)
        } else {
            null
        }
    }

    override fun getSpoolData(tag: Tag): FilamentSpool? {
        val mifare = MifareClassic.get(tag) ?: return null
        val uid = tag.id
        val uidHex = uid.joinToString("") { "%02X".format(it) }
        val A_keys = deriveAKeys(uid, mifare.sectorCount)

        return try {
            mifare.connect()
            val authenticatedSectors = mutableSetOf<Int>()
            val block2 = readBlock(mifare, 2, A_keys, authenticatedSectors) ?: return null
            val block4 = readBlock(mifare, 4, A_keys, authenticatedSectors) ?: return null
            val block5 = readBlock(mifare, 5, A_keys, authenticatedSectors) ?: return null
            val block13 = readBlock(mifare, 13, A_keys, authenticatedSectors) ?: return null
            val block16 = readBlock(mifare, 16, A_keys, authenticatedSectors) ?: return null
            // Data Extraction
            val filamentType = block2.toString(Charsets.UTF_8).trim { it <= ' ' }
            val fullFilamentType = block4.toString(Charsets.UTF_8).trim { it <= ' ' }
            val color1Bytes = block5.copyOfRange(0, 3)
            val color1Hex = "#" + color1Bytes.joinToString("") { "%02X".format(it) }
            val color2Bytes = block16.copyOfRange(4, 7)
            val color2Hex = "#" + color2Bytes.joinToString("") { "%02X".format(it) }
            val colorHex = if (color2Hex == "#000000") {
                color1Hex
            } else {
                "$color1Hex-$color2Hex"
            }
            val weightBuffer = ByteBuffer.wrap(block5, 4, 2).order(ByteOrder.LITTLE_ENDIAN)
            val spoolWeight = weightBuffer.short.toInt() and 0xFFFF
            val diameterBuffer = ByteBuffer.wrap(block5, 8, 4).order(ByteOrder.LITTLE_ENDIAN)
            val diameter = diameterBuffer.float.toDouble()
            val productionDate = parseProductionDate(block13)
            FilamentSpool(
                manufacturer = "Bambu Lab",
                material = filamentType.ifBlank { "Unknown" },
                colorName = fullFilamentType.ifBlank { filamentType.ifBlank { "Unknown" } },
                colorHex = colorHex,
                weightGrams = spoolWeight,
                diameterMm = diameter,
                productionDate = productionDate,
                uid = uidHex
            )
        } catch (e: Exception) {
            Log.e("BambuTag", "Failed to read Bambu tag", e)
            null
        } finally {
            try {
                mifare.close()
            } catch (_: Exception) {
            }
        }
    }

    fun dump(tag: Tag) {
        val mifare = MifareClassic.get(tag) ?: run {
            Log.e("BambuTag", "Tag does not support MifareClassic")
            return
        }
        val uid = tag.id
        val A_keys = deriveAKeys(uid, mifare.sectorCount)
        val authenticatedSectors = mutableSetOf<Int>()

        try {
            mifare.connect()
            for (sector in 0 until mifare.sectorCount) {
                val startBlock = mifare.sectorToBlock(sector)
                val blocksInSector = mifare.getBlockCountInSector(sector)
                for (offset in 0 until blocksInSector) {
                    val blockIndex = startBlock + offset
                    val data = readBlock(mifare, blockIndex, A_keys, authenticatedSectors)
                    if (data == null) {
                        Log.w("BambuTag", "Sector $sector block $blockIndex auth/read failed")
                        continue
                    }
                    val hex = data.joinToString(" ") { "%02X".format(it) }
                    Log.d("BambuTag", "Sector $sector block $blockIndex: $hex")
                }
            }
        } catch (e: Exception) {
            Log.e("BambuTag", "Failed to dump tag", e)
        } finally {
            try {
                mifare.close()
            } catch (_: Exception) {
            }
        }
    }
}