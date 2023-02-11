package com.jba.algorithm.search

import java.io.File

private var count = 0

/**
 * input is the path to a File containing an already sorted list of numbers
 *  output is the number of iterations required to find all the numbers in a list
 */
fun main() {
    File(readln()).readLines().map {
        it.toInt()
    }.let { numList ->
        numList.forEach {
            binarySearch(numbers = numList, num = it)
        }
    }
    println(count)
}

private fun binarySearch(numbers: List<Int>, num: Int) {
    var lIndex = 0
    var rIndex = numbers.size - 1
    while (lIndex <= rIndex) {
        count++
        val midIndex: Int = lIndex + (rIndex - lIndex) / 2
        if (numbers[midIndex] == num) return
        else if (numbers[midIndex] > num) rIndex = midIndex - 1
        else lIndex = midIndex + 1
    }
}
