package com.makaroni.lasermap

import android.app.Activity
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.snackbar.Snackbar
import com.makaroni.lasermap.utils.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var saveUri: Uri? = null

    private var map: MapView? = null
    private var button: Button? = null
    private var button2:Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        map = findViewById(R.id.map)
        button = findViewById(R.id.button)
        button2 = findViewById(R.id.button2)
        button?.setOnClickListener {
            openFile.launch(arrayOf("text/*"))
        }
        button2?.setOnClickListener {
            createFile.launch("${System.currentTimeMillis()}.png")
        }
        Log.d("TAG", "map = $map")
//        decode()
    }

    val save = registerForActivityResult(ActivityResultContracts.CreateDocument()) {
        saveUri = it
    }

    private fun decode(text: String) = scope.launch {
        Log.v("TAG", "decode")
//        val string =
//                withContext(Dispatchers.IO) { StringUtils.fromAsset("map_data.txt", this@MainActivity) }
        val decoded = LaserMapParser.decodeLRE(text)
        map?.data = decoded
        map?.createBitmap()
    }

    private val openFile = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        if (it != null)
            handleFile(it)
        else
            Log.d("TAG", "uri is null")
    }

    private val createFile = registerForActivityResult(ActivityResultContracts.CreateDocument()) {
        if (it ==null) return@registerForActivityResult
        else writeBytesToUri(it,map?.drawable?.toBitmap() ?: return@registerForActivityResult)
    }

    private fun handleFile(uri: Uri) {
        val file = FileUtils.getFileFromUri(uri, this) ?: return
        val text = FileUtils.readFile(file) ?: return
        decode(text)
    }

    private fun saveBitmap() {

    }

    private fun writeBitmapToUri() {

    }

    private fun writeBytesToUri(uri: Uri, bitmap: Bitmap) {
        val fileOutputStream = contentResolver.openOutputStream(uri) as FileOutputStream
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        try {
//            input.copyTo(fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
//            input.close()
            Snackbar.make(findViewById(R.id.ltRoot),"Saved",Snackbar.LENGTH_SHORT)
        } catch (e: Exception) {
            Snackbar.make(findViewById(R.id.ltRoot),"Error",Snackbar.LENGTH_SHORT)
            e.printStackTrace()
        }
    }
}