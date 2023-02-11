package com.jba.algorithm.search

import java.io.File
import kotlin.math.floor
import kotlin.math.sqrt

fun main() {
    File(readln()).readLines().map {
        it.toInt()
    }.let { jumpSearch(it) }
}


private fun jumpSearch(search: List<Int>) = run {
    val jump = floor(sqrt(search.lastIndex.toDouble())).toInt()
    val chunkedDirectory = search.chunked(jump)
    search.count { num ->
        chunkedDirectory.any { chunk ->
            chunk.any { it == num }
        }
    }
}
