package com.jba.algorithm.sort

import java.io.File
import java.util.*

fun main() = println(run { File(readln()).readLines().map { it.toInt() }.run { quickSort(this) } })
private fun quickSort(numbers: List<Int>, lIndex: Int = 0, rIndex: Int = numbers.size - 1) {
    val partitionIndex: Int
    if (lIndex < rIndex) {
        partitionIndex = partition(numbers, lIndex, rIndex)
        quickSort(numbers, lIndex, partitionIndex - 1)
        quickSort(numbers, partitionIndex + 1, rIndex)
    }
}

private fun partition(numbers: List<Int>, lIndex: Int, rIndex: Int): Int {
    val pivot = numbers[rIndex]
    var i = lIndex - 1
    for (j in lIndex until rIndex) {
        if (numbers[j] <= pivot) {
            i++
            Collections.swap(numbers, i, j)
        }
    }
    Collections.swap(numbers, i + 1, rIndex)
    return i + 1
}
