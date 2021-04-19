package com.makaroni.lasermap

import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.makaroni.lasermap.utils.FileUtils
import com.makaroni.lasermap.utils.StringUtils
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private var saveUri: Uri? = null

    var map:MapView? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)
        decode()
        map = findViewById<MapView>(R.id.map)
    }

    override fun onResume() {
        super.onResume()
        decode().start()
    }

    val save = registerForActivityResult(ActivityResultContracts.CreateDocument()) {
        saveUri = it
    }

    private fun decode() = scope.launch {
        Log.v("TAG", "decode")
        val string =
            withContext(Dispatchers.IO) { StringUtils.fromAsset("map_data.txt", this@MainActivity) }
        val decoded = LaserMapParser.decodeLRE(string)
        map?.data = decoded
        map?.invalidate()

//        FileUtils.writeToFile(decoded, this@MainActivity)

    }
}