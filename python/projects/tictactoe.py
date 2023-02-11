import re


def is_valid_grid():
    valid = True
    for e in grid_input:
        if e not in valid_cell or len(grid_input) != 9:
            valid = False
            break
    return valid


def draw_grid():
    print(border)
    for line in grid:
        print('| ' + ' '.join(line) + ' |')
    print(border)


def validate_move():
    if move is None or len(move) == 0 or not re.match(is_number, move):
        return 'You should enter numbers!'
    elif not re.match(is_valid_move, move):
        return 'Coordinates should be from 1 to 3!'
    return ''


def play(xo):
    col, row = move.split(' ')
    cell = grid[int(col) - 1][int(row) - 1]
    if cell != '_':
        return -1
    else:
        grid[int(col) - 1][int(row) - 1] = xo
        draw_grid()
    return 0


def find_winner():
    for line in grid:
        if is_unique(set(line[0:3])):
            return line[0]
    for pattern in winning_patterns:
        win = check_pattern(flatten(), pattern)
        if len(win) == 1:
            return win
    return ''


def check_pattern(string, pattern):
    cells = []
    for e in range(pattern[0], pattern[1] + 1, pattern[2]):
        cells.append(string[e])
    if is_unique(cells):
        return cells[0]
    return ''


def is_unique(row):
    unique = set(row)
    if len(unique) == 1 and unique != {'_'}:
        return True
    return False


def anybody_won(player) -> object:
    win = find_winner()
    if player == win:
        print(f'{player} wins')
        return True
    return False


def flatten():
    return [cell for sublist in grid for cell in sublist]


valid_cell = ['O', 'X', '_']
is_number = '^\\d \\d$'
is_valid_move = '^[1-3] [1-3]$'
winning_patterns = [[2, 6, 2], [0, 6, 3], [0, 8, 4], [1, 7, 3], [2, 8, 3]]
border = '-' * 9
grid_input = '_________'
if is_valid_grid():
    grid = [list(grid_input[0:3]),
            list(grid_input[3:6]),
            list(grid_input[6:9])]
    flattened_grid = flatten()
    draw_grid()
moves = ['X', 'O']
i = 0
while True:
    move = input()
    validation_result = validate_move()
    if validation_result != '':
        print(validation_result)
        continue
    next_move = moves[i]
    if play(next_move) != 0:
        print('This cell is occupied! Choose another one!')
        continue
    elif anybody_won(next_move):
        break
    if '_' not in flatten():
        print("Draw")
        break
    i = abs(i - 1)
