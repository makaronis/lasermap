package com.makaroni.lasermap

import android.opengl.Matrix
import android.renderscript.Matrix4f
import android.util.Log
import com.makaroni.lasermap.utils.ByteUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder

object LaserMapParser {

    const val rectangleLength = "02 95"

    fun parse(bytes: ByteArray) {
        val bf = ByteBuffer.wrap(ByteArray(bytes.size)).order(ByteOrder.LITTLE_ENDIAN)
    }

    fun decodeLRE(input: String):List<List<Byte>> {
        val regex = Regex(".{2}")
        val data = input.replace(" ", "")
        val result = regex.findAll(data).flatMap { it.groupValues }.zipWithNext().toList()
        val pairs = result.getCorrectPairs()
        val decodedList = decodeLrePairs(pairs)
        val length = 665//getLength()
        return createMatrix(length,decodedList)
    }

    private fun createMatrix(length: Int, data: List<Byte>):List<List<Byte>> {
        val lists = data.chunked(length)
        lists[1][2]
        return lists
    }

    private fun getLength(): Short {
        val length = ByteUtils.hexToBytes(rectangleLength)
        val bf = ByteBuffer.wrap(ByteArray(length.size)).order(ByteOrder.LITTLE_ENDIAN)
        return bf.short
    }

    fun decodeLrePairs(input: List<Pair<ByteArray, ByteArray>>): List<Byte> {
        val decodedArray = mutableListOf<Byte>()
        input.forEach { pair ->
            val count = ByteUtils.byteToUnsignedInt(pair.second.first())
            val byte = pair.first.first()
            for (i in 0..count) {
                decodedArray.add(byte)
            }
        }
        return decodedArray
    }

    private fun List<Pair<String, String>>.getCorrectPairs(): List<Pair<ByteArray, ByteArray>> {
        val pairs = mutableListOf<Pair<ByteArray, ByteArray>>()
        this.forEachIndexed { index, pair ->
            if (index % 2 != 0) return@forEachIndexed
            else pairs.add(
                Pair(
                    ByteUtils.hexToBytes(pair.first),
                    ByteUtils.hexToBytes(pair.second)
                )
            )
        }
        return pairs
    }
}