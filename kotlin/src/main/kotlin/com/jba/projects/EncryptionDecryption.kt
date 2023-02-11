package com.jba.projects

import java.io.File
import java.io.FileNotFoundException

private val upper = ('A'.hashCode()..'Z'.hashCode()).toList()
private val lower = ('a'.hashCode()..'z'.hashCode()).toList()
private val argKeys = listOf("-mode", "-key", "-data", "-alg", "-in", "-out")

fun main(args: Array<String>) {
    try {
        val argMap = getParams(args)
        val output = changeCrypt(argMap)
        writeOutput(argMap["-out"], output)
    } catch (e: FileNotFoundException) {
        print(e.message)
    }
}

fun getParams(args: Array<String>): Map<String, String> {
    val argMap = mutableMapOf<String, String>()
    for (arg in argKeys) {
        argMap[arg] = when (arg) {
            "-mode" -> if (args.indexOf(arg) == -1) "enc" else args[args.indexOf(arg) + 1]
            "-key", "-data", "-in", "-out", "-alg" -> {
                if (args.indexOf(arg) == -1) "" else args[args.indexOf(arg) + 1]
            }

            else -> continue
        }
    }
    return argMap
}

fun changeCrypt(argMap: Map<String, String>): String {
    val inData = argMap["-in"]?.let {
        if (it.isBlank()) "" else File(it).readText()
    }
    val key = if (argMap["-mode"] == "enc") {
        argMap["-key"]!!.toInt()
    } else argMap["-key"]!!.toInt() * -1
    var data = argMap["-data"]
    if (data!!.isEmpty()) {
        data = inData!!.ifEmpty { "" }
    }
    val output = if (argMap["-alg"] == "unicode") {
        applyUnicodeShift(data, key)
    } else applyShift(data, key)
    return String(output)
}

private fun applyShift(data: String, key: Int): CharArray {
    var output = charArrayOf()
    for (i in data.indices) {
        val charHash = data[i].hashCode()
        if (charHash !in (upper + lower)) {
            output += data[i]
            continue
        }
        val hashCodes = if (charHash in upper) upper else lower
        output += if (key < 0) {
            if (charHash + key < hashCodes.first()) {
                hashCodes[charHash + key - hashCodes.first() + hashCodes.size].toChar()
            } else hashCodes[charHash + key - hashCodes.first()].toChar()
        } else {
            if (charHash + key > hashCodes.last()) {
                hashCodes[charHash + key - hashCodes.first() - hashCodes.size].toChar()
            } else hashCodes[charHash + key - hashCodes.first()].toChar()
        }
    }
    return output
}

private fun applyUnicodeShift(data: String, key: Int): CharArray {
    val output = CharArray(data.length)
    for (i in data.indices) {
        output[i] = (data[i].code + key).toChar()
    }
    return output
}

@Throws(FileNotFoundException::class)
private fun writeOutput(filename: String?, output: String) {
    if (filename.isNullOrBlank()) {
        println(output)
    } else {
        File(filename).writeText(output)
    }
}
