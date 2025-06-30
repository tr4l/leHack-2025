const fs = require('fs');


function parseAndReplace(input, flag) {
    let result = '';
    let fc = 0;
    for (let i = 0; i < input.length; i++) {
        if (input[i] === flag[fc]) {
            result += replacementChar(flag[fc]);
            fc++;
        } else {
            result += input[i];
        }
    }
    // If the flag is not fully matched, return an error message
    if (fc < flag.length) {

        console.error("Error: Flag not fully matched " + flag.length + " " + fc);
        process.exit(1);
    }

    return result;
}


function replacementChar(char) {
    if (char >= 'a' && char <= 'z') {
        return char.toUpperCase();
    }
    if (char >= 'A' && char <= 'Z') {
        return char.toLowerCase();
    }

    switch (char) {
        case '{':
            return '[';
        case '}':
            return ']';
        case ' ':
            return ',';
        case '_':
            return ' ';

        default:
            break;
    }
    let intChar = parseInt(char)
    if (intChar >= 0 && intChar <= 5) {
        return intChar + 1;
    }
    if (intChar >= 6 && intChar <= 9) {
        return intChar - 1;
    }

    console.error("Invalid character:" + char);
    console.error("Add a mapping char, or remove the character from the input string");
    process.exit(1);
    return char;
}

const targetFlag = process.argv[2];
const filename = process.argv[3];
const inputString = fs.readFileSync(filename, 'utf-8');

console.log(parseAndReplace(inputString, targetFlag));

