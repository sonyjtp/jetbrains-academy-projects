package com.jba.projects

import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.floor
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis

fun main() {
    var directory = getPhoneRecords()
    val names = File("D:/find.txt").readLines()
    sortAndSearch(directory, names, Algorithm(Sort.NONE, Search.LINEAR))
    sortAndSearch(directory, names, Algorithm(Sort.BUBBLE, Search.JUMP))
    directory = getPhoneRecords()
    sortAndSearch(directory, names, Algorithm(Sort.QUICK, Search.BINARY))
    directory = getPhoneRecords()
    sortAndSearch(directory, names, Algorithm(Sort.NONE, Search.HASH))
}

private fun getPhoneRecords(filePath: String = "D:/directory.txt"): List<PhoneRecord> =
    File(filePath).readLines().map {
        PhoneRecord(it.substringAfter(' '), it.substringBefore(' ').toInt())
    }

private fun sortAndSearch(directory: List<PhoneRecord>, names: List<String>, algorithm: Algorithm) {
    var count: Int
    var searchTime: Long = 0
    val sortTime: Long
    when {
        algorithm.sort == Sort.NONE && algorithm.search == Search.LINEAR -> {
            println("Start searching (linear search)...")
            searchTime = measureTimeMillis { count = SearchService.linearSearch(directory, names) }
            printStats(algorithm, TimeStats(searchTime), count, names.size)
        }

        algorithm.sort == Sort.BUBBLE && algorithm.search == Search.JUMP -> {
            println("\nStart searching (bubble sort + jump search)...")
            var isSorted: Boolean
            val sortedDirectory = directory.apply {
                sortTime = measureTimeMillis {
                    isSorted = SortService.bubbleSort(this, searchTime)
                }
            }
            searchTime = measureTimeMillis {
                count = if (isSorted) {
                    SearchService.jumpSearch(sortedDirectory, names)
                } else SearchService.linearSearch(directory, names)
            }
            printStats(algorithm, TimeStats(searchTime, sortTime), count, names.size)
        }

        algorithm.sort == Sort.QUICK && algorithm.search == Search.BINARY -> {
            println("\nStart searching (quick sort + binary search)...")
            val sortedDirectory = directory.toMutableList()
            sortTime = measureTimeMillis { SortService.quickSort(sortedDirectory) }
            searchTime = measureTimeMillis {
                count = SearchService.binarySearch(sortedDirectory, names)
            }
            printStats(algorithm, TimeStats(searchTime, sortTime), count, names.size)
        }

        algorithm.search == Search.HASH -> {
            println("Start searching (hash table)...")
            var hashedDirectory: HashSet<String>
            val hashTime = measureTimeMillis { hashedDirectory = SortService.createHash(directory) }
            searchTime = measureTimeMillis { count = SearchService.hashSearch(hashedDirectory, names) }
            printStats(algorithm, TimeStats(searchTime, hashTime), count, names.size)
        }
    }
}

private fun printStats(algorithm: Algorithm, timeStats: TimeStats, found: Int = 0, total: Int) {
    print("Found $found / $total entries. ")
    println("Time taken: ${calculateTimeTaken(timeStats.search + timeStats.sort)}")
    calculateTimeTaken(timeStats.search + timeStats.sort)
    if (algorithm.sort != Sort.NONE) {
        print("Sorting time: ${calculateTimeTaken(timeStats.sort)}")
        println("Searching time: ${calculateTimeTaken(timeStats.search)}")
    }
    if (algorithm.search == Search.HASH) {
        print("Creating time: ${calculateTimeTaken(timeStats.sort)}")
        println("Searching time: ${calculateTimeTaken(timeStats.search)}")
    }
}

private fun calculateTimeTaken(timeTaken: Long) = "${TimeUnit.MILLISECONDS.toMinutes(timeTaken)} min. " +
        "${TimeUnit.MILLISECONDS.toSeconds(timeTaken) % 60} sec. ${timeTaken % 1000} ms."

private const val LATENCY_FACTOR = 10

object SortService {
    fun bubbleSort(phoneRecords: List<PhoneRecord>, linearSearchTime: Long): Boolean {
        var timeTaken: Long = 0
        var isSorted = true
        return phoneRecords.let {
            for (i in it.indices) {
                timeTaken += measureTimeMillis {
                    for (j in 1 until it.size - i) {
                        if (it[j - 1].name > it[j].name) Collections.swap(it, j - 1, j)
                    }
                }
                if (timeTaken > LATENCY_FACTOR * linearSearchTime) {
                    isSorted = false
                    break
                }
            }
            isSorted
        }
    }

    fun quickSort(
        phoneRecords: MutableList<PhoneRecord>, lIndex: Int = 0,
        rIndex: Int = phoneRecords.size - 1
    ) {
        val partitionIndex: Int
        if (lIndex < rIndex) {
            partitionIndex = partition(phoneRecords, lIndex, rIndex)
            quickSort(phoneRecords, lIndex, partitionIndex - 1)
            quickSort(phoneRecords, partitionIndex + 1, rIndex)
        }
    }

    fun createHash(searchIn: List<PhoneRecord>) =
        searchIn.map { it.name.lowercase() }.toHashSet()

    private fun partition(phoneRecords: MutableList<PhoneRecord>, lIndex: Int, rIndex: Int): Int {
        val pivot = phoneRecords[rIndex].name.lowercase()
        var i = lIndex - 1
        for (j in lIndex until rIndex) {
            if (phoneRecords[j].name.lowercase() <= pivot) {
                i++
                Collections.swap(phoneRecords, i, j)
            }
        }
        Collections.swap(phoneRecords, i + 1, rIndex)
        return i + 1
    }

}

object SearchService {

    fun linearSearch(searchIn: List<PhoneRecord>, searchFor: List<String>) = run {
        searchFor.count { find -> searchIn.any { item -> item.name == find } }
    }

    fun jumpSearch(searchIn: List<PhoneRecord>, searchFor: List<String>) = run {
        val jump = floor(sqrt(searchIn.lastIndex.toDouble())).toInt()
        val chunkedDirectory = searchIn.chunked(jump)
        searchFor.count { nameToFind ->
            chunkedDirectory.any { chunk ->
                chunk.any { it.name.equals(nameToFind, ignoreCase = true) }
            }
        }
    }

    fun binarySearch(searchIn: List<PhoneRecord>, searchFor: List<String>) = run {
        searchFor.count { nameToFind ->
            searchIn.let {
                var lIndex = 0
                var rIndex = it.size - 1
                var foundMatch = false
                while (!foundMatch && lIndex <= rIndex) {
                    val mIndex = lIndex + (rIndex - lIndex) / 2
                    if (it[mIndex].name.equals(nameToFind, ignoreCase = true)) {
                        foundMatch = true
                    } else if (it[mIndex].name.lowercase() > nameToFind.lowercase()) rIndex = mIndex - 1
                    else lIndex = mIndex + 1
                }
                foundMatch
            }
        }
    }

    fun hashSearch(searchIn: Set<String>, searchFor: List<String>): Int = searchFor.count {
        searchIn.contains(it.lowercase())
    }
}

class TimeStats(val search: Long, val sort: Long = -1)

enum class Search { LINEAR, JUMP, BINARY, HASH }

enum class Sort { BUBBLE, QUICK, NONE }

data class Algorithm(val sort: Sort, val search: Search)

data class PhoneRecord(val name: String, val phoneNumber: Int)
