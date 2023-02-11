package com.jba.algorithm.search

import java.io.File

fun main() = File(readln()).readLines().map { it.toInt() }.let { println(linearSearch(it)) }

private fun linearSearch(searchIn: List<Int>) = run { searchIn.count { find -> searchIn.any { it == find } } }
