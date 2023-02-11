package com.jba.projects

import com.jba.projects.ChessBoard.cells
import kotlin.math.abs

const val SIZE = 8
const val DEST_ROW_IX = 3
val colIndexes = ('a'..'h').toList()

fun main() {
    println("Pawns-Only Chess")
    Game.play()
    println("Bye!")
}

object Game {
    private lateinit var player1: Player1
    private lateinit var player2: Player2
    private lateinit var player: Player
    fun play() {
        getPlayers() // get player names
        ChessBoard.init() // add pawns to cells and display the intial chessboard
        var continueGame = true
        while (continueGame) { // continue if game is not over or if user has not chosen to exit
            player = getNextPlayer()
            val move = player.playNextTurn()
            if ("exit" == move) {
                continueGame = false
            } else {
                refreshDisplay(move, player.getColor()) // update the chessboard with the new move
                if (isGameOver()) continueGame = false // isGameOver = true when its a stalemate or if we have a winner
            }
        }
    }

    private fun getPlayers() {
        println("First Player's name:")
        player1 = Player1(readln())
        println("Second Player's name:")
        player2 = Player2(readln())
        player = player2
    }

    private fun getNextPlayer(): Player { // for the next round
        return when (player) {
            is Player1 -> player2
            is Player2 -> player1
            else -> throw (ExceptionInInitializerError("Unknown instance of Player"))
        }
    }

    private fun isGameOver(): Boolean = hasWinner() || isStalemate()

    /**
     *  If the number of  current player's pawns = number of current player's
     *  blocked pawns and that pawn cannot be captured by any other pawn
     *  @returns {boolean}  - true if stalemate
     */
    private fun isStalemate(): Boolean { //
        val nonEmptyCells = ChessBoard.getNonEmptyCells() //Map of : row[0-7]col[0-7] to [B|W]
        val blockedCells = mutableMapOf("W" to 0, "B" to 0) // initialize blocked cell count for each color to 0
        val nonEmptyCellCountsByColor = mutableMapOf("W" to 0, "B" to 0) // map of count of W and B left
        for ((rowCol, value) in nonEmptyCells) {
            nonEmptyCellCountsByColor[value] = nonEmptyCellCountsByColor[value]!! + 1
            when (value) {
                "W" -> if (nonEmptyCells.containsKey("${rowCol[0] + 1}${rowCol[1]}") //if there is a pawn in front ..
                    && !isPossibleCapture(nonEmptyCells, value, rowCol, 1)
                ) // ...which cannot be captured by another pawn of the same color
                    blockedCells[value] = blockedCells[value]!! + 1 // add it to blocked cells
                "B" -> if (nonEmptyCells.containsKey("${rowCol[0] - 1}${rowCol[1]}")
                    && !isPossibleCapture(nonEmptyCells, value, rowCol, -1)
                )
                    blockedCells[value] = blockedCells[value]!! + 1

                else -> continue
            }
        }
        val isBlocked = blockedCells[getNextPlayer() // if the blocked pawn has the next move --> stalemate
            .getColor()[0].toString()] == nonEmptyCellCountsByColor[getNextPlayer().getColor()[0].toString()]
        if (isBlocked) println("Stalemate!")
        return isBlocked
    }

    /**
     *   Not En Passant capture. Checking if the blocking pawn can be captured by another pawn of the same color
     *   @param nonEmptyCells - occupied cells
     *   @param color  - current player's pawn color
     *    @returns {boolean} - true if the blocking pawn is a candidate for capture
     * */
    private fun isPossibleCapture(
        nonEmptyCells: Map<String, String>, color: String,
        rowCol: String, direction: Int
    ): Boolean {
        val row = rowCol[0].toString().toInt() + direction
        return when (rowCol[1]) {
            '0' -> nonEmptyCells.containsKey("$row${1}") && nonEmptyCells["$row${1}"] != color // W's base
            '7' -> nonEmptyCells.containsKey("$row${SIZE - 2}") && nonEmptyCells["$row${SIZE - 2}"] != color //B's base
            else -> {
                val leftCol = rowCol[1].toString().toInt() - 1 // one cell diagonally to the left
                val rightCol = rowCol[1].toString().toInt() + 1 // one cell diagonally to the right
                (nonEmptyCells.containsKey("$row$leftCol") && nonEmptyCells["$row$leftCol"] != color)
                        || nonEmptyCells.containsKey("$row$rightCol") && nonEmptyCells["$row$rightCol"] != color
            }
        }

    }

    /**
     *  If any of the pawn's of of a player are in the opponent's first row, or if none of the other player's
     *  pawns are left, print the winner.
     *  @returns {boolean} returns true if we have a winner so that the play can be stopped
     */
    private fun hasWinner(): Boolean {
        val winner = when {
            cells[SIZE - 1].joinToString("").trim().isNotEmpty() // B's first row
                    || cells.none { player2.getColor()[0] in it.joinToString("") } -> player1.getColor()

            cells[0].joinToString("").trim().isNotEmpty() // W's first row
                    || cells.none { player1.getColor()[0] in it.joinToString("") } -> player2.getColor()

            else -> ""
        }
        return if (winner.isNotEmpty()) {
            println("$winner Wins!")
            true
        } else false
    }


    private fun refreshDisplay(move: String, color: String) { // update the moves after each move
        ChessBoard.updateDisplay(move, color)
    }
}

abstract class Player(private val name: String, private val color: String) {

    fun playNextTurn(): String {
        println("$name's turn:")
        var move = readln()
        while ("exit" != move && !isValidMove(move)) {
            println("$name's turn:")
            move = readln()
        }
        updateEnPassant(move)
        return move
    }

    /**
     * Validations are done in the `Validator` singleton. Check for captures if the move
     * is valid
     * @param move - the 4-char move
     * @returns {boolean} - true if the move is valid
     */
    private fun isValidMove(move: String): Boolean {
        return when {
            !Validator.validate(move, color) || !isWithinBounds(move) -> false
            else -> checkForCaptures(move)
        }
    }

    /**
     * Check for normal captures and en-passant captures
     * @param move - the 4-char move
     * @returns {boolean} - true if a capture occurred.
     */
    private fun checkForCaptures(move: String): Boolean {
        var isValid = true
        if (abs(move[0].code - move[2].code) == 1) {
            if (abs(move[1] - move[DEST_ROW_IX]) == 1) {
                if (!isNormalCapture(move) && (ChessBoard.enPassantCandidate == -1 || !isEnpassant(move))) {
                    println("Invalid Input")
                    isValid = false
                }
            }
        }
        return isValid
    }

    /** Non-En Passant capture*/
    private fun isNormalCapture(move: String): Boolean {
        val destValue = ChessBoard.getCellValueByPosition(move.substring(2, DEST_ROW_IX + 1))
        return destValue.isNotEmpty() && this.color[0].toString() != destValue
    }

    /** En Passant capture*/
    private fun isEnpassant(move: String): Boolean {
        return if (abs(ChessBoard.enPassantCandidate - colIndexes.indexOf(move[0])) == 1) {
            ChessBoard.updateCell(move[1].toString().toInt() - 1, ChessBoard.enPassantCandidate)
            true
        } else false
    }

    /**
     * For En Passant capture, in addition to moving the pawn, the captured pawn cell has to be cleared
     */
    private fun updateEnPassant(move: String) {
        if (abs(move[DEST_ROW_IX].code - move[1].code) == 2) {
            ChessBoard.enPassantCandidate = colIndexes.indexOf(move[0])
        } else if (ChessBoard.enPassantCandidate != -1) ChessBoard.enPassantCandidate = -1
    }

    fun getColor() = this.color
    fun getName() = this.name

    /**
     * Initial validation A move is within bounds if:
     * - the pawn was moved forward.
     * - the pawn doesn't go beyond the 8 x 8 boundary
     */
    abstract fun isWithinBounds(move: String): Boolean
}

class Player1(name: String, color: String = "White") : Player(name, color) {

    override fun isWithinBounds(move: String): Boolean {
        return if (Validator.isMovingForward(move, direction = 1) && Validator.isNotStretching(move, startRow = 2)) {
            true
        } else {
            println("Invalid Input")
            false
        }
    }
}

class Player2(name: String, color: String = "Black") : Player(name, color) {

    override fun isWithinBounds(move: String): Boolean {
        return if (Validator.isMovingForward(move, direction = -1)
            && Validator.isNotStretching(move, startRow = SIZE - 1)
        ) {
            true
        } else {
            println("Invalid Input")
            false
        }
    }
}

object ChessBoard {

    private const val CORNER = '+'
    private const val ROW_DIVIDE = "---"
    private const val COL_DIVIDE = '|'
    private const val BLACK_CELL = " B "
    private const val WHITE_CELL = " W "
    private const val BLANK_CELL = "   "
    private const val HORIZONTAL_LINES = 18
    private const val BLACK_DEFAULT_INDEX = 6
    private const val WHITE_DEFAULT_INDEX = 1
    var cells = mutableListOf<MutableList<String>>()
    private var boardLines = mutableListOf<String>()
    var enPassantCandidate: Int = -1

    fun init() {
        for (row in 0 until SIZE) {
            cells.add(when (row) {
                WHITE_DEFAULT_INDEX -> MutableList(SIZE) { WHITE_CELL }
                BLACK_DEFAULT_INDEX -> MutableList(SIZE) { BLACK_CELL }
                else -> MutableList(SIZE) { BLANK_CELL }
            })
        }
        draw()
    }

    /**
     * Draw the chessboard after each move and before 1st move. The pawn positions are determined by
     * `cells`. All pawn moves will be updated in `cells`
     */
    private fun draw() {
        var j = SIZE
        boardLines = mutableListOf()
        for (row in 0 until HORIZONTAL_LINES) {
            boardLines.add(
                when {
                    row == HORIZONTAL_LINES - 1 -> "    a   b   c   d   e   f   g   h"
                    row % 2 == 1 -> {
                        val k = j--
                        "$k $COL_DIVIDE${getRowCells(k - 1)}"
                    }

                    else -> "  ${(CORNER + ROW_DIVIDE).repeat(SIZE)}$CORNER"
                }
            )
        }
        display()
    }

    /** print the board */
    private fun display() = boardLines.forEach(::println)

    fun updateDisplay(move: String, color: String) {
        updateCells(move, color)
        draw()
    }

    /** cells with pawns as a HashMap of cell co-ordinates and pawn color*/
    fun getNonEmptyCells(): Map<String, String> {
        val nonEmptyCells = mutableMapOf<String, String>()
        for (row in 1..SIZE) {
            for (col in colIndexes) {
                val cellValue = getCellValueByPosition("$col$row")
                if (cellValue.trim().isNotEmpty()) nonEmptyCells["${row - 1}${colIndexes.indexOf(col)}"] = cellValue
            }
        }
        return nonEmptyCells //[0-7][0-7] to [B|W]
    }

    fun getCellValueByPosition( //col(a..7)row(1..8)
        position: String
    ) = cells[position[1].toString().toInt() - 1][colIndexes.indexOf(position[0])].trim()

    /**
     * @param row - row index
     * @returns {string}  - cells in the same row */
    private fun getRowCells(row: Int): String {
        val builder: StringBuilder = StringBuilder()
        for (col in 0 until SIZE) {
            builder.append("${cells[row][col]}$COL_DIVIDE")
        }
        return builder.toString()
    }

    /** update `cells` array with pawns */
    private fun updateCells(move: String, color: String) {
        updateCell(move[1].toString().toInt() - 1, colIndexes.indexOf(move[0]))
        updateCell(move[DEST_ROW_IX].toString().toInt() - 1, colIndexes.indexOf(move[2]), " ${color[0]} ")
    }

    fun updateCell(row: Int, col: Int, newVal: String = BLANK_CELL) {
        cells[row][col] = newVal
    }
}

object Validator {
    private const val MOVE_OUT_OF_BOUNDS = 3

    fun validate(move: String, color: String): Boolean {
        return when {
            !matchRegex(move) -> {
                println("Invalid Input")
                false
            }

            !hasPawnAtSource(move, color) -> {
                println("No $color pawn at ${move.substring(0, 2)}")
                false
            }

            isBlocked(move) -> {
                println("Invalid Input")
                false
            }

            else -> true
        }
    }

    fun isMovingForward(move: String, direction: Int): Boolean {
        return abs(move[0] - move[2]) < 2
                && move[1].toString().toInt() in 2 until SIZE
                && (move[DEST_ROW_IX].code - move[1].code) * direction > 0
    }

    fun isNotStretching(move: String, startRow: Int): Boolean =
        move[1].toString().toInt() == startRow && abs(move[DEST_ROW_IX].code - move[1].code) < MOVE_OUT_OF_BOUNDS
                || move[1].toString().toInt() != startRow && abs(move[DEST_ROW_IX].code - move[1].code) == 1

    private fun matchRegex(
        input: String, regexStr: String = "[a-h][1-8][a-h][1-8]"
    ) = regexStr.toRegex().matches(input)

    private fun hasPawnAtSource(move: String, color: String) = ChessBoard.getCellValueByPosition(
        move.substring(0, 2)
    ) == color[0].toString()

    private fun isBlocked(move: String) = move[0] == move[2] && ChessBoard.getCellValueByPosition(
        move.substring(2, DEST_ROW_IX + 1)
    ).isNotEmpty()
}
