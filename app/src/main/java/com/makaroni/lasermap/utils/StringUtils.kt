package com.makaroni.lasermap.utils

import android.content.Context
import com.makaroni.lasermap.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

object StringUtils {

    @JvmStatic
    @Throws(IOException::class)
    fun fromAsset(path: String, ctx: Context): String {
        val buf = StringBuilder()
        val json = ctx.assets.open(path)
        val input = BufferedReader(InputStreamReader(json, StandardCharsets.UTF_8))
        var str: String? = input.readLine()
        while (str != null) {
            buf.append(str)
            str = input.readLine()
        }
        input.close()
        return buf.toString()
    }

}