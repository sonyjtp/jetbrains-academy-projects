import random
import string


def validate_pencil_count(pencils):
    for char in pencils:
        if char not in string.digits:
            print("The number of pencils should be numeric")
            return -1
    if int(pencils) == 0:
        print("The number of pencils should be positive")
        return -1
    else:
        return int(pencils)


def bot_play(remaining):
    move = 0
    if remaining == 1:
        move = 1
    elif remaining % 4 == 1:
        move = random.randint(1, 3)
    elif remaining % 4 == 0:
        move = 3
    elif remaining % 4 == 3:
        move = 2
    elif remaining % 4 == 2 or remaining == 1:
        move = 1
    print(f"{move}")
    return move


pencil_count = -1
while pencil_count < 0:
    pencil_count = validate_pencil_count(
        input('How many pencils would you like to use:'))
    if pencil_count < 0:
        continue
players = "John", "Jack"
index = -1
while index < 0:
    player = input(f'Who will be the first ({players[0]}, {players[1]}):')
    if player not in players:
        print("Choose between 'John' and 'Jack'")
        continue
    index = players.index(player)
print(''.join('|' * pencil_count))
print(f"{players[index]}'s turn:")
while pencil_count > 0:
    if players[index] == "Jack":
        number_input = bot_play(pencil_count)
    else:
        number_input = input()
        if number_input not in ['1', '2', '3']:
            print("Possible values: '1', '2' or '3'")
            continue
        elif int(number_input) > pencil_count:
            print("Too many pencils were taken")
            continue
    if int(number_input) == pencil_count:
        print(f'{players[abs(index - 1)]} won!')
        break
    pencil_count -= int(number_input)
    print(''.join('|' * pencil_count))
    index = abs(index - 1)
    print(f"{players[index]}'s turn:")
