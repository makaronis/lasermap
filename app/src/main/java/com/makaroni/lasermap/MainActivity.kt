package com.makaroni.lasermap

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.makaroni.lasermap.utils.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var saveUri: Uri? = null

    private var map: MapView? = null
    private var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        map = findViewById(R.id.map)
        button = findViewById(R.id.button)
        button?.setOnClickListener {
            openFile.launch(arrayOf("text/*"))
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
        map?.invalidate()
    }

    private val openFile = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
        if (it != null)
            handleFile(it)
        else
            Log.d("TAG","uri is null")
    }

    private fun handleFile(uri: Uri) {
        val file = FileUtils.getFileFromUri(uri, this) ?: return
        val text = FileUtils.readFile(file) ?: return
        decode(text)
    }
}