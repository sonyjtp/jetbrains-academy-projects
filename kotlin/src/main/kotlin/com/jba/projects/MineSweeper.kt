package com.jba.projects


import java.util.*

private const val GRID_SIZE = 9
private var displayedCells = Array(GRID_SIZE) { Array(GRID_SIZE) { "." } }
private var cells = Array(GRID_SIZE) { Array(GRID_SIZE) { "." } }
private var mineCoordinates = mutableListOf<String>()
private var freedCells = mutableListOf<String>()
private var correctGuesses = mutableSetOf<String>()
private var mineCount = 0
private val validInputRegex = Regex("^[1-9] [1-9] (mine|free)$")

fun main() {
    println("How many mines do you want on the field?")
    mineCount = readln().toInt()
    getMineCoordinates()
    drawBoard()
    play()
}

private fun getMineCoordinates() {
    while (mineCoordinates.size < mineCount) {
        val mine = Random().nextInt(0, GRID_SIZE * GRID_SIZE - 1)
        val coordinates = (mine / GRID_SIZE).toString() + (mine % GRID_SIZE).toString()
        if (coordinates !in mineCoordinates) {
            mineCoordinates.add(coordinates)
        }
    }
}

private fun recalculate(inputCoordinates: String) {
    while (inputCoordinates in mineCoordinates) {
        mineCoordinates = mutableListOf()
        getMineCoordinates()
        recalculate(inputCoordinates)
    }
    assignCellValues()
}

private fun play() {
    var isGameOver: Int
    isGameOver = playFirstRound()
    while (isGameOver == 0) {
        println("Set/unset mine marks or claim a cell as free:")
        isGameOver = markCell(readln().split(' ').chunked(2))
    }
    drawBoard()
    if (isGameOver == -1) {
        println("You stepped on a mine and failed!")
    } else println("Congratulations! You found all the mines!")
}

private fun playFirstRound(): Int {
    assignCellValues()
    var isValid = false
    var result = 0
    while (!isValid) {
        println("Set/unset mine marks or claim a cell as free:")
        val input = readln()
        if (!validInputRegex.matches(input)) {
            println("Invalid input")
            continue
        }
        isValid = true
        result = markCell(input.split(' ').chunked(2), true)
    }
    return result
}

private fun markCell(input: List<List<String>>, isFirstRound: Boolean = false): Int {
    val colRow = input[0].joinToString("") { "${it.toInt() - 1}" }
    val row = colRow[1].toString().toInt()
    val col = colRow[0].toString().toInt()
    var action = input[1].joinToString("")
    val currCellValue = getCellValue(colRow[0].toString().toInt(), colRow[1].toString().toInt())
    var retVal = 0
    if (isFirstRound && action == "free") action = "freeFirst"
    when (action) {
        "mine" -> {
            displayedCells[col][row] = if (currCellValue == "*") "." else "*"
            drawBoard()
            if (colRow in mineCoordinates) {
                correctGuesses.add(colRow)
                if (mineCoordinates.size == correctGuesses.size) retVal = 1
            }
        }

        "free" -> {
            when (colRow) {
                in mineCoordinates -> retVal = explodeMines()
                else -> {
                    freeCells()
                    freedCells.toSet().toList()
                    if (freedCells.count() == GRID_SIZE * GRID_SIZE - mineCount) retVal = 1
                }
            }
        }

        "freeFirst" -> {
            if (colRow in mineCoordinates) recalculate(colRow)
            retVal = markCell(input)
        }

        else -> println("Invalid action")
    }
    return retVal
}

private fun explodeMines(): Int {
    for (mine in mineCoordinates) {
        displayedCells[mine[0].toString().toInt()][mine[1].toString().toInt()] = "X"
    }
    drawBoard()
    return -1
}

private fun freeCells() {
    for (i in 0 until GRID_SIZE) {
        for (j in 0 until GRID_SIZE) {
            if (cells[j][i] == "X") continue
            freedCells.add(cells[j][i])
            displayedCells[j][i] = cells[j][i]
        }
    }
}

private fun getCellValue(col: Int, row: Int) = displayedCells[col][row]

private fun drawBoard() {
    println(" │123456789│")
    println("—│—————————│")
    for (row in 0 until GRID_SIZE) {
        print("${row + 1}|")
        for (col in 0 until GRID_SIZE) {
            print(displayedCells[col][row])
        }
        println("|")
    }
    println("—│—————————│")
}

private fun assignCellValues() {
    for (row in 0 until GRID_SIZE) {
        for (col in 0 until GRID_SIZE) {
            if ("$col$row" in mineCoordinates) cells[col][row] = "X"
            else {
                cells[col][row] = countMinesAround(col, row).let { if (it == "0") "/" else it }
            }

        }
    }
}

private fun countMinesAround(col: Int, row: Int): String = mineCoordinates.map { it }.intersect(
    cellsAround(col, row)
).size.toString()

private fun cellsAround(col: Int, row: Int): Set<String> {
    return setOf(
        "${col - 1}${row - 1}", "${col - 1}$row", "${col - 1}${row + 1}",
        "$col${row - 1}", "$col${row + 1}",
        "${col + 1}${row - 1}", "${col + 1}$row", "${col + 1}${row + 1}"
    )
}
