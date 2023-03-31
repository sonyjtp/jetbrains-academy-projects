package com.jba.projects

import java.io.File
import kotlin.system.measureTimeMillis


fun main(args: Array<String>) {
    val (algorithm, filename, pattern, result) = listOf(args[0], args[1], args[2], args[3])
    val contents = File("${System.getProperty("user.dir")}/$filename").readLines().joinToString("\n")
    val retVal: Boolean
    val timeTaken = measureTimeMillis {
        retVal = when {
            algorithm.uppercase().contains(Algorithm.KMP.toString()) -> compareUsingKmp(contents, pattern)
            algorithm.uppercase().contains(Algorithm.NAIVE.toString()) -> compareUsingNaive(contents, pattern)
            else -> throw IllegalArgumentException("Invalid argument: $algorithm")
        }
    } / 1000.000
    println(if (retVal) result else "Unknown file type")
    println("It took $timeTaken seconds")
}


private fun compareUsingKmp(fileContents: String, pattern: String): Boolean {
    val prefixMap = findPrefixMap(pattern)

    var (i, patternIndex, first) = listOf(0, 0,  0)
    while (i < fileContents.length) {
        if (fileContents[i] == pattern[patternIndex]) {
            if (patternIndex == pattern.length - 1) return true
            i ++
            patternIndex ++
        } else {
            if (patternIndex == 0) patternIndex = pattern.length
            i = first + patternIndex - prefixMap[patternIndex - 1]!!
            patternIndex = 0
            first = i
        }
    }
    return false
}

private fun compareUsingNaive (fileContents: String, pattern: String): Boolean {
    for ( i in 0 .. fileContents.length - pattern.length) {
        if (fileContents.substring(i, i + pattern.length).equals(pattern, true)) return true
    }
    return false
}



private fun findPrefixMap(str: String): Map<Int, Int> {
    val prefixMap = mutableMapOf<Int, Int>()
    var (i, j) = listOf(1, 0)
    prefixMap[0] = 0
    while (i < str.length) {
        if (str[i] == str[j]) {
            j ++
            prefixMap[i] = j
            i++
        }  else {
            if (j != 0) j = prefixMap[j - 1]!!
            else {
                prefixMap[i] = 0
                i++
            }
        }
    }
    return prefixMap
}

enum class Algorithm { NAIVE, KMP}




