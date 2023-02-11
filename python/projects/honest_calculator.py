def is_valid(calc_arr):
    try:
        if calc_arr[0] == 'M':
            calc_arr[0] = str(memory)
        if calc_arr[2] == 'M':
            calc_arr[2] = str(memory)
        float(calc_arr[0]) and float(calc_arr[2])
        return True
    except ValueError:
        return False


def validate_operands():
    valid = False
    while not valid:
        print("Enter an equation")
        calc_arr = ("" + input()).split()
        if not is_valid(calc_arr):
            print("Do you even know what numbers are? Stay focused!")
            continue
        elif calc_arr[1] not in ["+", "-", "*", "/"]:
            print("Yes ... an interesting math operation. You've slept through all classes, haven't you?")
            continue
        elif calc_arr[1] == '/' and float(calc_arr[2]) == 0.0:
            print("Yeah... division by zero. Smart move...")
            continue
        valid = True
    return calc_arr


def calculate():
    if oper == '+':
        return float(x) + float(y)
    elif oper == '-':
        return float(x) - float(y)
    elif oper == '*':
        return float(x) * float(y)
    else:
        return float(x) / float(y)


def store_result():
    while True:
        print("Do you want to store the result? (y / n):")
        store_calc = input()
        if store_calc == "y" or store_calc == "n":
            break
    return store_calc


memory = 0.0
exit_calc = False
while not exit_calc:
    calc = validate_operands()
    x, oper, y = calc
    result = calculate()
    print(result)
    save = store_result()
    continue_calc = ""
    while continue_calc != "y" and continue_calc != "n":
        print("Do you want to continue calculations? (y / n):")
        continue_calc = input()
    if continue_calc == "n":
        exit_calc = True
