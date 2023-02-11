const input = require('sync-input')
let wQty = 400, mQty = 540, cQty = 120, cups = 9, cash = 550;

let displayStock = () => {
    console.log("The coffee machine has: ");
    console.log(`${wQty} ml of water`);
    console.log(`${mQty} ml of milk`);
    console.log(`${cQty} g of coffee beans`);
    console.log(`${cups} disposable cups`);
    console.log(`$${cash} of money\n`)
}

let takeStock = (w, m, c) => {
    let noStock = "";
    if (w > wQty) noStock = "water";
    if (m > mQty) noStock = "milk";
    if (c > cQty) noStock = "coffee beans";
    return noStock;
}

let buy = (choice) => {
    let sold = 0;
    switch (choice) {
        case "1":
            sold = updateQty(250, 0, 16, 4);
            break;
        case "2":
            sold = updateQty(350, 75, 20, 7);
            break;
        case "3":
            sold = updateQty(200, 100, 12, 6);
            break
        case "4":
            sold = updateQty(250, 20, 16, 5);
            break;

    }
    cups -= sold;
}
let fill = () => {
    wQty += parseInt(input("\nWrite how many ml of water you want to add: "));
    mQty += parseInt(input("Write how many ml of milk you want to add: "));
    cQty += parseInt(input("Write how many grams of coffee beans you want to add: "));
    cups += parseInt(input("Write how many disposable cups you want to add: "));
}

let take = () => {
    console.log(`I gave you $${cash}\n`)
    cash = 0;
}

let updateQty = (w, m, c, amt) => {
    let depleted = takeStock(w, m, c);
    if (depleted !== '') {
        console.log(`Sorry, not enough ${depleted}!\n`);
        return 0;
    }
    console.log("I have enough resources, making you a coffee!\n");
    wQty -= w;
    mQty -= m;
    cQty -= c;
    cash += amt
    return 1;
}

let execute = (axn) => {
    switch (axn) {
        case "buy":
            let choice = input("\nWhat do you want to buy? " +
                "1 - espresso, 2 - latte, 3 - cappuccino, 4 - macchiato: ");
            buy(choice);
            break;
        case "fill":
            fill();
            break;
        case "take":
            take();
            break;
        case "remaining":
            displayStock();
            break;
        case "exit":
            return true;
    }
    return false;
}

let exit = false;
while (!exit) {
    exit = execute(input("Write action (buy, fill, take, remaining, exit): "));
}
