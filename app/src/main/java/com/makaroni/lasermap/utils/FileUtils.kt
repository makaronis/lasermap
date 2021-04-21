package com.makaroni.lasermap.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import com.google.android.material.snackbar.Snackbar
import com.makaroni.lasermap.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.text.CharacterIterator
import java.text.StringCharacterIterator
import java.util.*


object FileUtils {



    @JvmStatic
    @Throws(IOException::class)
    fun readBinaryFileFromAsset(context: Context, path: String): ByteArray {
        return context.assets.open(path).readBytes()
    }

    fun getHumanReadableByteCountSI(inputBytes: Long): String? {
        var bytes = inputBytes
        if (-1000 < bytes && bytes < 1000) {
            return "$bytes B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }
        return java.lang.String.format(
                Locale.getDefault(),
                "%.1f %cB",
                bytes / 1000.0,
                ci.current()
        )
    }

    @JvmStatic
    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme.equals("content")) {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                Log.e("FileUtils", "failed to read file name", e)
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf(File.separator) ?: -1
            if (cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result.toString()
    }

    fun File.getMimeType(fallback: String = "image/*"): String {
        return MimeTypeMap.getFileExtensionFromUrl(toString())
                ?.run { MimeTypeMap.getSingleton().getMimeTypeFromExtension(toLowerCase()) }
                ?: fallback // You might set it to */*
    }

    fun getMimeType(uri: Uri, context: Context): String? {
        var mimeType: String? = null
        mimeType = if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr: ContentResolver = context.applicationContext.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(
                    uri
                            .toString()
            )
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase(Locale.getDefault())
            )
        }
        return mimeType
    }

    fun getFileFromUri(uri: Uri, context: Context): File? {
        val inputStream = context.contentResolver.openInputStream(uri) ?: return null
        val fileName = getFileName(context, uri)
        val dir = context.cacheDir
//        val file = File.createTempFile(fileName, "", dir)
        val file = File(dir, fileName)
        val out: OutputStream = FileOutputStream(file)
        val buf = ByteArray(1024)
        var len: Int
        while (inputStream.read(buf).also { len = it } > 0) {
            out.write(buf, 0, len)
        }
        out.close()
        inputStream.close()
        return file
    }

    fun getFileSize(file: File?): Long {
        if (file == null || !file.exists()) return 0
        if (!file.isDirectory) return file.length()
        val dirs: MutableList<File> = LinkedList()
        dirs.add(file)
        var result: Long = 0
        while (dirs.isNotEmpty()) {
            val dir = dirs.removeAt(0)
            if (!dir.exists()) continue
            val listFiles = dir.listFiles()
            if (listFiles == null || listFiles.isEmpty()) continue
            for (child in listFiles) {
                result += child.length()
                if (child.isDirectory) dirs.add(child)
            }
        }
        return result
    }

    suspend fun File.getByteArray(): ByteArray {
        val size = this.length().toInt()
        val bytes = ByteArray(size)
        withContext(Dispatchers.IO) {
            try {
                val buf = BufferedInputStream(FileInputStream(this@getByteArray))
                buf.read(bytes, 0, bytes.size)
                buf.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return bytes
    }

    suspend fun Bitmap.getByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        val byteArray = withContext(Dispatchers.IO) {
            this@getByteArray.compress(Bitmap.CompressFormat.PNG, 100, stream)
            this@getByteArray.recycle()
            stream.toByteArray()
        }
        return byteArray
    }

    fun readFile(file: File): String? {
        val text = StringBuilder()
        return try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                text.append('\n')
            }
            br.close()
            text.toString()
        } catch (e: IOException) {
            null
            //You'll need to add proper error handling here
        }
    }
}