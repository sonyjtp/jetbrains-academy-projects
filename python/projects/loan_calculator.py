import argparse
import math


def calc_annuity():
    nom_i = float(i) / (100 * 12)
    if principal is None or principal == '':
        pymt = float(payment)
        n = int(period)
        p = math.floor((pymt / (nom_i * pow(1 + nom_i, n) / (pow(1 + nom_i, n) - 1))))
        print(f"Your loan principal = {math.floor(p)}!")
        print(f'Overpayment = {math.floor(pymt * n - p)}')
    elif payment is not None and payment != '':
        pymt = float(payment)
        p = float(principal)
        months = math.ceil(math.log(pymt / (pymt - nom_i * p), (1 + nom_i)))
        print(f"It will take you {math.floor(months / 12)} years ", end="")
        print(f"and {months % 12} months ", end="") if months % 12 > 0 else print("", end="")
        print("to repay this loan!")
        print(f'Overpayment = {math.floor(months * pymt - p)}')
    else:
        p = float(principal)
        n = float(period)
        annuity = math.ceil(p * nom_i * pow(1 + nom_i, n) / (pow(1 + nom_i, n) - 1))
        print(f"Your annuity payment = {annuity}!")
        print(f'Overpayment = {math.floor(annuity * n - p)}')


def calc_diff():
    nom_i = float(i) / (100 * 12)
    n = int(period)
    p = float(principal)
    total = 0
    for m in range(1, n + 1):
        diff = math.ceil(p / n + nom_i * (p - (p * (m - 1)) / n))
        print(f"Month {m}: payment is {diff}")
        total += diff
    print(f'\nOverpayment = {math.floor(total - p)}')


def is_valid():
    if i is None or i == '' or float(i) < 0:
        return False
    if pymnt_type is None or pymnt_type == '' or pymnt_type not in ['annuity', 'diff']:
        return False
    if pymnt_type == 'diff':
        if payment is not None:
            return False
        if period is None or period == '' or int(period) < 0:
            return False
    elif (principal is None or principal == '' or float(principal) < 0) \
            and (period is None or period == '' or int(period) < 0):
        return False
    return True


def add_args():
    parser = argparse.ArgumentParser()
    parser.add_argument('--type')
    parser.add_argument('--payment')
    parser.add_argument('--principal')
    parser.add_argument('--periods')
    parser.add_argument('--interest')
    return parser.parse_args()


args = add_args()
pymnt_type = args.type
payment = args.payment
principal = args.principal
period = args.periods
i = args.interest

if not is_valid():
    print("Incorrect parameters.")
else:
    match pymnt_type:
        case 'diff':
            calc_diff()
        case 'annuity':
            calc_annuity()
