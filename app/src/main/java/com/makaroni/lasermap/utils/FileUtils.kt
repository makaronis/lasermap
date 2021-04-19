package com.makaroni.lasermap.utils

import android.content.Context
import android.util.Log
import java.io.IOException
import java.io.OutputStreamWriter

object FileUtils {

    fun writeToFile(data: String, context: Context) {
        try {
            val outputStreamWriter =
                OutputStreamWriter(context.openFileOutput("lasermap-${System.currentTimeMillis()}.txt", Context.MODE_PRIVATE))
            outputStreamWriter.write(data)
            outputStreamWriter.close()
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }
}