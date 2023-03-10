package com.jba.projects

import java.io.File

private val allWords = mutableListOf<String>()
private val candidateWords = mutableListOf<String>()
private const val VALID_WORD_LENGTH = 5

fun main(args: Array<String>) {
    validateArgs(args)?.let {
        if (validateWords(it.first, it.second)) {
            println("Words Virtuoso")
            play()
        }
    }
}

fun play() {
    var exit = -1
    val secretWord = candidateWords[(0 until candidateWords.size).random()]
    while (exit < 0) {
        println("Input a 5-letter word:")
        val guess = readln()
        if ("exit".equals(guess, true)) break
        if (validateGuess(guess, secretWord)) {
            println("Correct!")
            exit = 0
        }
    }
    if (exit == -1) println("The game is over.")
}

fun validateGuess(guess: String, secretWord: String): Boolean {
    return when {
        guess.equals(secretWord, true) -> true
        guess.length != VALID_WORD_LENGTH -> {
            println("The input isn't a 5-letter word.")
            false
        }

        !isWord(guess) -> {
            println("One or more letters of the input aren't valid.")
            false
        }

        hasDuplicateLetters(guess) -> {
            println("The input has duplicate letters.")
            false
        }

        guess.lowercase() !in allWords -> {
            println("The input word isn't included in my words list.")
            false
        }

        else -> {
            compareWords(guess, secretWord)
            false
        }
    }
}

private fun compareWords(guess: String, secretWord: String) {
    val printVal = mutableListOf<String>()
    for (i in guess.indices) {
        printVal.add(
            when (guess[i]) {
                secretWord[i] -> guess[i].uppercase()
                in secretWord -> guess[i].lowercase()
                else -> "_"
            }
        )
    }
    println(printVal.joinToString(""))
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

private fun isValid(word: String) = isWord(word) && !hasDuplicateLetters(word)

private fun isWord(word: String) = Regex("^[a-zA-Z]{5}$").matches(word)

private fun hasDuplicateLetters(word: String) = word.length != word.toCharArray().toSet().size
