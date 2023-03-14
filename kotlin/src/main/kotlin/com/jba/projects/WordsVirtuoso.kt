package com.jba.projects

import java.io.File
import kotlin.math.round
import kotlin.system.measureTimeMillis

private val allWords = mutableListOf<String>()
private val candidateWords = mutableListOf<String>()
private val previousClues = mutableListOf<String>()
private val wrongLetters = mutableSetOf<String>()
private const val VALID_WORD_LENGTH = 5

fun main(args: Array<String>) {
    validateArgs(args)?.let {
        if (validateWords(it.first, it.second)) {
            println("Words Virtuoso\n")
            play()
        }
    }
}

private fun play() {
    var exit = -1
    var tries = 1
    val secretWord = candidateWords[(0 until candidateWords.size).random()]
    val timeTaken = measureTimeMillis {
        while (exit < 0) {
            println("Input a 5-letter word:")
            val guess = readln()
            println("\n")
            if ("exit".equals(guess, true)) break
            if (validateGuess(guess, secretWord)) exit = 0 else tries += 1
        }
    }
    if (exit == -1) println("The game is over.")
    else {
        println("${secretWord.map { color(it.uppercase(), Color.GREEN) }.joinToString("")}\nCorrect!\n")
        if (tries == 1) println("Amazing luck! The solution was found at once.")
        else println("The solution was found after $tries tries in ${round(timeTaken / 1000.0).toInt()} seconds.")
    }
}

private fun validateGuess(guess: String, secretWord: String): Boolean {
    var retVal = false
    when {
        guess.equals(secretWord, true) -> {
            println(previousClues.joinToString("\n"))
            retVal = true
        }

        guess.length != VALID_WORD_LENGTH -> println("The input isn't a 5-letter word.")
        !isWord(guess) -> println("One or more letters of the input aren't valid.")
        hasDuplicateLetters(guess) -> println("The input has duplicate letters.")
        guess.lowercase() !in allWords -> println("The input word isn't included in my words list.")
        else -> compareWords(guess, secretWord)
    }
    return retVal
}

private fun compareWords(guess: String, secretWord: String) {
    var printVal = mutableListOf<String>()
    for (i in guess.indices) {
        printVal.add(
            when (guess[i]) {
                secretWord[i] -> color(guess[i].uppercase(), Color.GREEN)
                in secretWord -> color(guess[i].uppercase(), Color.YELLOW)
                else -> {
                    wrongLetters.add(guess[i].uppercase())
                    color(guess[i].uppercase(), Color.GREY)
                }
            }
        )
    }
    val clueString = printVal.joinToString("")
    previousClues.add(clueString)
    println(previousClues.joinToString("\n"))
    if (wrongLetters.isNotEmpty()) {
        println("\n${
            color(wrongLetters.toMutableList().let {
                it.sort()
                it
            }.joinToString(""), Color.AZURE)
        }\n"
        )
    }
}


private fun validateArgs(args: Array<String>): Pair<File, File>? {
    if (args.size != 2) {
        println("Error: Wrong number of arguments.")
        return null
    }
    val wordsFile = File(args[0])
    if (!wordsFile.exists() || !wordsFile.isFile) {
        println("Error: The words file ${args[0]} doesn't exist.")
        return null
    }
    val candidateWordsFile = File(args[1])
    if (!candidateWordsFile.exists() || !candidateWordsFile.isFile) {
        println("Error: The candidate words file ${args[1]} doesn't exist.")
        return null
    }
    return Pair(wordsFile, candidateWordsFile)
}

private fun validateWords(wordsFile: File, candidateWordsFile: File): Boolean {
    val words = wordsFile.readLines()
    var invalidWordCount = 0
    var invalidCandidateCount = 0
    var missingWordCount = 0
    words.forEach { word -> if (!isValid(word)) invalidWordCount++ }
    if (invalidWordCount != 0) {
        println("Error: $invalidWordCount invalid words were found in the ${wordsFile.name} file.")
    } else {
        allWords.addAll(words)
        candidateWordsFile.readLines().forEach { candidate ->
            var isValid = isValid(candidate)
            if (!isValid) invalidCandidateCount++
            if (candidate.uppercase() !in words && candidate.lowercase() !in words) {
                isValid = false
                missingWordCount++
            }
            if (isValid) candidateWords.add(candidate)
        }
        if (invalidCandidateCount > 0) {
            println("Error: $invalidCandidateCount invalid words were found in the ${candidateWordsFile.name} file.")
        } else if (missingWordCount > 0) {
            println("Error: $missingWordCount candidate words are not included in the $wordsFile file.")
        }
    }
    return invalidWordCount == 0 && invalidCandidateCount == 0 && missingWordCount == 0
}

private fun color(str: String, color: Color): String {
    return when (color) {
        Color.GREEN -> "\u001B[48:5:10m${str.uppercase()}\u001B[0m"
        Color.YELLOW -> "\u001B[48:5:11m${str.uppercase()}\u001B[0m"
        Color.GREY -> "\u001B[48:5:7m${str.uppercase()}\u001B[0m"
        Color.AZURE -> "\u001B[48:5:14m${str}\u001B[0m"
    }
}

private fun isValid(word: String) = isWord(word) && !hasDuplicateLetters(word)

private fun isWord(word: String) = Regex("^[a-zA-Z]{5}$").matches(word)

private fun hasDuplicateLetters(word: String) = word.length != word.toCharArray().toSet().size

private enum class Color { GREEN, YELLOW, GREY, AZURE }
