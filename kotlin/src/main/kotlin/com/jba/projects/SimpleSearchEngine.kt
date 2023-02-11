package com.jba.projects

import java.io.File

fun main(args: Array<String>) {
    val people = readFile(args)
    val searchIndex = buildInvertedIndex(readFile(args))

    while (true) {
        when (getChoice()) {
            1 -> findMatches(people, searchIndex)
            2 -> printPeople(people)
            0 -> break
        }
    }
    println("Bye!")
}

fun buildInvertedIndex(lines: List<String>): List<Pair<String, List<Int>>> {
    return lines.flatMapIndexed { index: Int, line: String ->
        line.split(" ").filter { word ->
            word.isNotBlank() && word[0].toString().toIntOrNull() == null
        }.map { index to it }
    }.groupBy {
        it.second
    }.map { (name, value) ->
        name to value.map { lineNumListNamePair -> lineNumListNamePair.first }
    }
}

fun readFile(args: Array<String>): List<String> = File(args[1]).readLines()

private fun getChoice(): Int {
    printOptions()
    var choice = readln()
    while (choice !in listOf("0", "1", "2")) {
        println("Incorrect option! Try again.")
        printOptions()
        choice = readln()
    }
    return choice.toInt()
}

private fun findMatches(people: List<String>, peopleIndex: List<Pair<String, List<Int>>>) {
    val strategy = getStrategy()
    println("Enter a name or email to search all suitable people.")
    val searchList = readln().split(" ")
    val matches = when (strategy) {
        Strategy.ALL -> matchAll(peopleIndex, searchList)
        Strategy.ANY -> matchAny(peopleIndex, searchList).toSet().toList()
        Strategy.NONE -> matchNone(people.indices.toList(), peopleIndex, searchList).toSet().toList()
    }
    if (matches.isEmpty()) {
        println("No matching people found")
    } else {
        val noun = if (matches.size > 1) "persons" else "person"
        println("${matches.size} $noun found")
        for (index in matches) {
            println(people[index])
        }
    }
}

fun getStrategy(): Strategy {
    println("Select a matching strategy: ALL, ANY, NONE")
    return Strategy.valueOf(readln().uppercase())
}

fun matchAll(peopleIndex: List<Pair<String, List<Int>>>, searchList: List<String>): List<Int> {
    val matchAny = matchAny(peopleIndex, searchList)
    return matchAny.groupingBy { it }.eachCount().filter {
        it.value == searchList.size
    }.keys.toList()
}

fun matchNone(
    peopleIndices: List<Int>, peopleIndex: List<Pair<String,
            List<Int>>>, searchList: List<String>
): List<Int> {
    return peopleIndices.minus(matchAny(peopleIndex, searchList).toSet())
}

private fun matchAny(
    peopleIndex: List<Pair<String,
            List<Int>>>, searchList: List<String>
): List<Int> {
    val matches = mutableListOf<Int>()
    for (searchWord in searchList) {
        matches.addAll(matchString(peopleIndex, searchWord))
    }
    return matches.toList()
}

private fun matchString(
    peopleIndex: List<Pair<String,
            List<Int>>>, searchWord: String
): List<Int> {
    return peopleIndex.filter {
        it.first.uppercase() == searchWord.uppercase()
    }.map { it.second }.toList().flatten()
}

private fun printOptions() {
    println("=== Menu ===")
    println("1. Find a person")
    println("2. Print all people")
    println("0. Exit")
}

fun printPeople(people: List<String>) = people.forEach { println(it) }

enum class Strategy { ALL, ANY, NONE }
