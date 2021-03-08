package ru.eecode.poems.utils

import android.content.Context
import java.io.IOException
import java.io.InputStream
import java.nio.charset.Charset

class JsonAssetsLoader(private val context: Context) {
    fun load(path: String): String? {
        return try {
            val inputStream: InputStream = context.assets.open(path)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }
    }
}