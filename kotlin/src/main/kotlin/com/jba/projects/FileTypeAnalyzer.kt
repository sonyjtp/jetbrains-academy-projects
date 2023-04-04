package com.jba.projects

import java.io.File


fun main(args: Array<String>) {
    File("${System.getProperty("user.dir")}/${args[0]}").listFiles()?.forEach { file ->
        val searchEngine = Thread(SearchEngine(algorithm = "KMP", file, pattern = args[1].trim(), docType = args[2]))
        searchEngine.start()
        searchEngine.join()
    }
}


class SearchEngine(
    private val algorithm: String, private val file: File,
    private val pattern: String, private val docType: String
) : Runnable {

    @Synchronized
    private fun compareUsingKmp(): Boolean {
        val prefixMap = findPrefixMap(pattern)
        var (i, patternIndex, first) = listOf(0, 0, 0)
        val contents = file.readLines().joinToString("\n")
        while (i < contents.length) {
            if (contents[i] == pattern[patternIndex]) {
                if (patternIndex == pattern.length - 1) return true
                i++
                patternIndex++
            } else {
                if (patternIndex == 0) patternIndex++
                i = first + patternIndex - prefixMap[patternIndex - 1]!!
                patternIndex = 0
                first = i
            }
        }
        return false
    }

    private fun compareUsingNaive(): Boolean {
        val contents = file.readLines().joinToString("\n")
        for (i in 0..contents.length - pattern.length) {
            if (contents.substring(i, i + pattern.length).equals(pattern, true)) return true
        }
        return false
    }

    private fun findPrefixMap(str: String): Map<Int, Int> {
        val prefixMap = mutableMapOf<Int, Int>()
        var (i, j) = listOf(1, 0)
        prefixMap[0] = 0
        while (i < str.length) {
            if (str[i] == str[j]) {
                j++
                prefixMap[i] = j
                i++
            } else {
                if (j != 0) j = prefixMap[j - 1]!!
                else {
                    prefixMap[i] = 0
                    i++
                }
            }
        }
        return prefixMap
    }

    override fun run() {
        val match = when (SearchAlgorithm.valueOf(algorithm.uppercase())) {
            SearchAlgorithm.NAIVE -> compareUsingNaive()
            SearchAlgorithm.KMP -> compareUsingKmp()
        }
        println("${file.name}: ${if (match) docType else "Unknown file type"}")
    }
}

enum class SearchAlgorithm { NAIVE, KMP }
