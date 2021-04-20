package com.makaroni.lasermap

import com.makaroni.lasermap.utils.ByteUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder

object LaserMapParser {

    const val rectangleLength = "02 95"

    fun parse(bytes: ByteArray) {
        val bf = ByteBuffer.wrap(ByteArray(bytes.size)).order(ByteOrder.LITTLE_ENDIAN)
    }

    fun decodeLRE(input: String): List<List<Byte>> {
        val data = input.replace(" ", "").replace("\n", "")
        val byteList = ByteUtils.hexToBytes(data)
        val pairs = byteList.toMutableList().zipWithNext().toList().getCorrectPairs().toMutableList()
        val sizePairs = pairs[2]
        val filteredPairs = pairs.slice(3 until pairs.size)
        val decodedList = decodeLrePairs(filteredPairs)
        val length = getLength(sizePairs)
        return createMatrix(length, decodedList)
    }

    private fun createMatrix(length: Int, data: List<Byte>) = data.chunked(length)

    private fun getLength(pair: Pair<Byte, Byte>): Int {
        val bytes = byteArrayOf(pair.second, pair.first)
        val bf = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        val result = ByteUtils.parseTwoBytesInt(bytes)
        return result
    }

    fun decodeLrePairs(input: List<Pair<Byte, Byte>>): List<Byte> {
        val decodedArray = mutableListOf<Byte>()
        input.forEach { pair ->
            val count = ByteUtils.byteToUnsignedInt(pair.second)
            decodedArray.addAll(List(count) {
                return@List pair.first
            })
        }
        return decodedArray
    }

    private fun List<Pair<Byte, Byte>>.getCorrectPairs(): List<Pair<Byte, Byte>> {
        val pairs = mutableListOf<Pair<Byte, Byte>>()
        this.forEachIndexed { index, pair ->
            if (index % 2 != 0) return@forEachIndexed
            else pairs.add(pair)
        }
        return pairs
    }

    fun writeMapToFile(data: List<Byte>, size: Int) {

    }
}