package com.jba.projects

private val fullDeck = getFullDeck()
private val cardsOnTable = getCardsOnTable()
private var availableDeck = fullDeck.filter { it !in cardsOnTable }.toMutableList()
private var isFirstTurn = true
private const val DEAL_COUNT = 6

private const val EXIT = "exit"
private lateinit var player1: CardPlayer
private lateinit var player2: CardPlayer

private var lastWinner = ""

fun main() {
    println("Indigo Card Game")
    var continuePlaying = true // if player chooses to exit, continuePlaying = false
    while (availableDeck.size >= DEAL_COUNT && continuePlaying) {
        if (isFirstTurn) {
            decideWhoPlaysFirst()
            isFirstTurn = false
        }
        Dealer.dealCards()
        continuePlaying = playNextRound()
    }
    if (continuePlaying) {
        if (cardsOnTable.size > 0)
            println("${cardsOnTable.size} cards on the table, and the top card is ${cardsOnTable.last()}")
        PointsCalculator.calculate()
        displayPoints() // final points display before the game ends
    }
    println("Game Over")
}

private fun decideWhoPlaysFirst() {
    var playFirst = ""
    while (playFirst.uppercase() !in listOf("YES", "NO")) { // loop until you get a yes/no
        println("Play first?")
        playFirst = readln().lowercase()
        if ("YES".equals(playFirst, ignoreCase = true)) {
            player1 = Human()
            player2 = Computer()
        } else if ("NO".equals(playFirst, ignoreCase = true)) {
            player1 = Computer()
            player2 = Human()
        }
    }
    println("Initial cards on the table: ${cardsOnTable.joinToString(" ")}")
}

private fun playNextRound(): Boolean { // next set of 6 from the new deal
    val cardsInHandCount = player1.cardsOwned.size
    var i = 0
    var exit = false
    while (i < cardsInHandCount && !exit) {
        when (player1.name) {
            "Player" -> {
                exit = playTurn(player1)
                if (!exit) playTurn(player2) // no turn for Computer if player chooses to exit
            }

            "Computer" -> {
                playTurn(player1)
                exit = playTurn(player2) // if player chooses to exit, exit = true
            }
        }
        i++
    }
    return !exit // inverting the boolean since the variable
    // that accepts this is named continuePlaying
}

private fun playTurn(player: CardPlayer): Boolean {
    println()
    var topCard = " "
    if (cardsOnTable.isEmpty()) {
        println("No cards on the table")
    } else {
        topCard = cardsOnTable.last()
        println("${cardsOnTable.size} cards on the table, and the top card is $topCard")
    }
    val cardsOwned = player.cardsOwned
    val choice = player.play() // play the card if it's the last one
    if (EXIT == choice) return true
    cardsOwned.remove(choice) // remove card that was played
    cardsOnTable.add(choice)
    if (doCardsMatch(choice, topCard)) { // we have a match!
        println("${player.name} wins cards")
        player.cardsWon.addAll(cardsOnTable)
        PointsCalculator.addPoints(player)
        if (availableDeck.size > 0 || cardsOwned.size > 0 || player2.cardsOwned.size > 0) {
            displayPoints()
        }
        lastWinner = player.name
        cardsOnTable.clear()
    }
    return false
}

fun doCardsMatch(playedCard: String, topCard: String): Boolean {
    return playedCard.last() == topCard.last()
            || playedCard.substring(0, playedCard.length - 1) == topCard.substring(0, topCard.length - 1)
}

private fun getFullDeck(): List<String> {
    val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
    val suits = listOf("♠", "♥", "♦", "♣")
    return suits.map { suit -> ranks.map { rank -> "$rank$suit" } }.flatten().shuffled()
}

private fun getCardsOnTable(n: Int = 4) = fullDeck.toMutableList().subList(0, n).toMutableList()

private fun displayPoints() {
    if ("Player" == player1.name) {
        println("Score: Player ${player1.pointsWon} - Computer ${player2.pointsWon}")
        println("Cards: Player ${player1.cardsWon.size} - Computer ${player2.cardsWon.size}")
    } else {
        println("Score: Player ${player2.pointsWon} - Computer ${player1.pointsWon}")
        println("Cards: Player ${player2.cardsWon.size} - Computer ${player1.cardsWon.size}")
    }
}

object Dealer {

    fun dealCards() {
        if (availableDeck.size >= DEAL_COUNT) { // stop dealing if there are no more cards
            var i = 0
            while (i < DEAL_COUNT) {
                deal(player1)
                deal(player2)
                i += 1
            }
        }
    }

    private fun deal(player: CardPlayer) {
        val nextCard = availableDeck.first()
        player.cardsOwned.add(nextCard)
        availableDeck.remove(nextCard)
    }
}

object PointsCalculator {
    private val specialCards = listOf("A", "10", "J", "Q", "K")
    private const val EXTRA_POINTS = 3

    fun calculate() {
        addPointsForRemainingCards()
        addPointsForMoreCards()
    }

    fun addPoints(player: CardPlayer) {
        for (card in cardsOnTable) {
            if (card.substring(0, card.length - 1) in specialCards)
                incrementPoints(player)
        }
    }

    private fun addPointsForRemainingCards() {
        when (lastWinner) {
            "", player1.name -> {
                player1.cardsWon.addAll(cardsOnTable)
                addPoints(player1)
            }

            else -> {
                player2.cardsWon.addAll(cardsOnTable)
                addPoints(player2)
            }
        }
    }

    private fun addPointsForMoreCards() {
        if (player1.cardsWon.size >= player2.cardsWon.size) {
            incrementPoints(player1, EXTRA_POINTS)
        } else incrementPoints(player2, EXTRA_POINTS)
    }

    private fun incrementPoints(player: CardPlayer, points: Int = 1) {
        player.pointsWon += points
    }
}

abstract class CardPlayer {
    open val name = ""
    var cardsOwned = mutableListOf<String>()
    var cardsWon = mutableListOf<String>()
    var pointsWon = 0

    abstract fun play(): String

    fun printOwnedCards() {
        cardsOwned.map {
            "${
                if (this.name == "Player") {
                    (this.cardsOwned.indexOf(it) + 1).toString() + ')'
                } else ""
            }$it "
        }.forEach(::print)
        println()
    }
}

class Human : CardPlayer() {
    override val name = "Player"

    override fun play(): String {
        print("Cards in hand: ")
        printOwnedCards()
        var choice = "0"
        while (choice.toIntOrNull() == null // loop until player chooses 1..6
            || choice.toInt() !in 1..cardsOwned.size
        ) {
            print("Choose a card to play (1-${cardsOwned.size}):")
            choice = readln()
            if (EXIT == choice) return EXIT
        }
        return cardsOwned[choice.toInt() - 1]
    }
}

class Computer : CardPlayer() {
    override val name = "Computer"

    override fun play(): String {
        printOwnedCards()
        val nextCard = if (cardsOwned.size == 1) cardsOwned[0] else getNextCard()
        println("Computer plays $nextCard")
        return nextCard
    }

    private fun getNextCard() = when {
        cardsOnTable.isEmpty() -> getCardWhenNoMatches()
        else -> findMatches()
    }

    private fun getCardWhenNoMatches() = getSimilarCards().shuffled().first()

    private fun getSimilarCards(): List<String> {
        var similarCards = cardsOwned.groupBy {
            it.last()
        }.filter {
            it.value.size > 1
        }.values.flatten()
        if (similarCards.isEmpty()) {
            similarCards = cardsOwned.groupBy {
                it.substring(0, it.length - 1)
            }.filter {
                it.value.size > 1
            }.values.flatten()
        }
        if (similarCards.isEmpty()) similarCards = cardsOwned
        return similarCards
    }

    private fun findMatches(): String {
        val candidateCards = getCandidates()
        return candidateCards.ifEmpty { getSimilarCards() }.shuffled().first()
    }

    private fun getCandidates(): List<String> {
        val topCard = cardsOnTable.last()
        val suitMatches = cardsOwned.filter { card ->
            card.last() == topCard.last()
        }
        val rankMatches = cardsOwned.filter { card ->
            card.substring(0, card.length - 1) == topCard.substring(0, topCard.length - 1)
        }
        return when {
            suitMatches.size > 1 -> suitMatches
            rankMatches.size > 1 -> rankMatches
            else -> suitMatches + rankMatches
        }
    }
}
