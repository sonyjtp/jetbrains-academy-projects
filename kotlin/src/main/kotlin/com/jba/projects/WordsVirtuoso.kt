import java.io.File

fun main() {
    println("Input the words file:")
    val input = readln()
    val file = File(input)
    if(!file.exists()) {
        println("Error: The words file $input doesn't exist.")
    } else {
        validateWords(file)
    }
}

fun validateWords(file: File) {
    val lines = file.readLines()
    var invalidCount = 0
    for (word in lines) {
        if (!isValid(word.trim())) invalidCount ++
    }
    if (invalidCount == 0) println("All words are valid!")
    else println("Warning: $invalidCount invalid words were found in the " +
            "${file.name} file.")
}

private fun isValid(str: String): Boolean
{
    return Regex("^[a-zA-Z]{5}$").matches(str)
            && str.length == str.toCharArray().toSet().size
}
