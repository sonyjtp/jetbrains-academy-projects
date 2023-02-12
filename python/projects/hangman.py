import os
import random
import re


def display_menu():
    print('H A N G M A N\n')
    params = {}
    results = {"wins": 0, "losses": 0}
    while True:
        choice = input('Type "play" to play the game, "results" to show the scoreboard, '
                       'and "exit" to quit:\n')
        match choice:
            case "play":
                params = reset(random.choice(words))
                print(''.join(params['filled']))
                if play_game(params):
                    print(f'You guessed the word {params["word"]}!\nYou survived!\n')
                    results['wins'] += 1
                else:
                    print('You lost!\n')
                    results['losses'] += 1
            case "results":
                display_results(results)
            case "exit":
                params['exit'] = True
                break
    return params


def reset(word):
    return {
        'attempts_left': 8,
        'filled': ['-' for x in word],
        'word': word,
        'guesses': [],
        'exit': False
    }


def play_game(params):
    while params['attempts_left'] > 0:
        letter = input('Input a letter:')
        inc_attempt = handle_guess(params, letter)
        params['attempts_left'] -= inc_attempt
        if ''.join(params['filled']) == params['word']:
            return True
        if params['attempts_left'] > 0:
            print(''.join(params['filled']))
    return False


def update_filled(params, guess):
    for i in range(0, len(params['word'])):
        if params['filled'][i] != '-':
            continue
        if params['word'][i] == guess:
            params['filled'][i] = guess


def is_valid_letter(guessed):
    if len(guessed) != 1:
        print('Please, input a single letter.\n')
        return False
    if not re.match('^[a-z]*$', guessed):
        print('Please, enter a lowercase letter from the English alphabet.\n')
        return False
    return True


def handle_guess(params, guess):
    if not is_valid_letter(guess):
        return 0
    if guess in params['guesses']:
        print("You've already guessed this letter.\n")
    elif guess in params['word']:
        update_filled(params, guess)
        print()
    else:
        params['guesses'].append(guess)
        print("That letter doesn't appear in the word.\n")
        return 1
    params['guesses'].append(guess)
    return 0


def display_results(results):
    print(f'You won: {results["wins"]} times\nYou lost: {results["losses"]} times')


words = ['python', 'java', 'swift', 'javascript']
game_params = display_menu()

