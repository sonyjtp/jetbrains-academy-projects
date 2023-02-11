package com.jba.algorithm.sort

import java.io.File
import java.util.*


fun main() = println(run { File(readln()).readLines().map { it.toInt() }.apply { bubbleSort(this) } })

private fun bubbleSort(listToSort: List<Int>) {
    return listToSort.let {
        for (i in it.indices) {
            for (j in 1 until it.size - i) {
                if (it[j - 1] > it[j]) Collections.swap(it, j - 1, j)
            }
        }
    }
}
