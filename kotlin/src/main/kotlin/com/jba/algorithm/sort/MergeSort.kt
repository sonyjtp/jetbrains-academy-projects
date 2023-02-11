package com.jba.algorithm.sort

import kotlin.math.floor
import kotlin.random.Random
import kotlin.system.measureTimeMillis

fun main() {
    println("How many numbers to sort ?")
    val numbers = MutableList(readln().toInt()) { Random.nextInt(1, 100000) }.toIntArray()
    println("Unsorted numbers: ${numbers.joinToString(",")}")
    val timeTaken = measureTimeMillis { mergeSort(numbers) }
    println("Sorted numbers: ${numbers.joinToString(",")}")
    println("Time taken to sort ${numbers.size} numbers: ${timeTaken / 1000.000} seconds.")
}

private fun mergeSort(numbers: IntArray) {
    if (numbers.size > 1) {
        val mid = floor((numbers.size / 2).toDouble()).toInt()
        val left = numbers.copyOfRange(0, mid)
        val right = numbers.copyOfRange(mid, numbers.size)
        mergeSort(left)
        mergeSort(right)
        var (i, j, k) = MutableList(3) { 0 }
        while (i < left.size && j < right.size) numbers[k++] = if (left[i] <= right[j]) left[i++] else right[j++]
        while (i < left.size) numbers[k++] = left[i++]
        while (j < right.size) numbers[k++] = right[j++]
    }
}
