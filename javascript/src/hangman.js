const input = require('sync-input');

let langs = ["python", "java", "swift", "javascript"];
const lowercase = new RegExp('[a-z]');
let continuePlaying = true;
let attemptsLeft;
let word;
let filled;
let revealed;
let lettersGuessed;
let wins = 0;
let losses = 0;

let setCharAt = (str, index, chr) => str.substring(0, index) + chr + str.substring(1 + index, str.length);

let displayMenu = () => {
    while (continuePlaying) {
        let choice = input('Type "play" to play the game, "results" to show the scoreboard, and "exit" to quit:');
        switch (choice) {
            case "play":
                reset();
                playGame();
                break;
            case "results":
                displayResult();
                break;
            case "exit":
                continuePlaying = false;
                break;
        }
    }
}
let reset = () => {
    attemptsLeft = 8;
    word = langs[Math.floor((Math.random() * langs.length))];
    filled = '-'.repeat(word.length);
    revealed = '';
    lettersGuessed = '';
    console.log();
}

let playGame = () => {
    console.log(filled);
    while (attemptsLeft > 0) {
        let letter = input("Input a letter: ");
        if (isValidInput(letter)) {
            lettersGuessed = lettersGuessed + letter;
            handleGuess(letter);
        }
        console.log();
        if (attemptsLeft > 0) console.log(filled);
    }
    if (revealed != null && revealed === word) {
        wins++;
        console.log(`You guessed the word ${word}!`);
        console.log("You survived!");
    } else {
        losses++;
        console.log("You lost!");
    }
}

let displayResult = () => {
    console.log(`You won: ${wins} times.`);
    console.log(`You lost: ${losses} times.`);
}
let isValidInput = (guess) => {
    let isValid = true;
    if (guess.length !== 1) {
        isValid = false;
        console.log("Please, input a single letter.");
    } else if (!lowercase.test(guess)) {
        isValid = false;
        console.log("Please, enter a lowercase letter from the English alphabet.");
    } else if (lettersGuessed.length > 0 && lettersGuessed.indexOf(guess) > -1) {
        isValid = false;
        console.log("You've already guessed this letter.");
    }
    return isValid;
}

let updateFilled = (guess) => {
    for (let i = 0; i < word.length; i++) {
        if (filled[i] !== '-') continue;
        if (word[i] === guess) {
            filled = setCharAt(filled, i, guess);
        }
    }
}

let handleGuess = (guess) => {
    if (word.indexOf(guess) < 0) {
        console.log("That letter doesn't appear in the word.");
        attemptsLeft--;
    } else if (revealed.length > 0 && revealed.indexOf(guess) > -1) {
        console.log("No improvements.");
        attemptsLeft--;
    } else {
        updateFilled(guess);
        revealed = filled.replaceAll('-', '');
        if (revealed === word || filled.indexOf('-') < 0) attemptsLeft = 0;
    }
}

console.log('H A N G M A N');
displayMenu();
