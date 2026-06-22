"use strict";

const cnst = value => (...args) => value;
const variable = v => (...args) => args[variables[v]];

const operation = f => (...args) => (...vals) => f(...args.map(arg => arg(...vals)));
const findIndex = (cmp) => operation((...args) => args.indexOf(cmp(...args)));

const add = operation((x, y) => x + y);
const subtract = operation((x, y) => x - y);
const multiply = operation((x, y) => x * y);
const divide = operation((x, y) => x / y);
const negate = operation(x => -x);

const argMin3 = findIndex(Math.min);
const argMax3 = findIndex(Math.max);
const argMin5 = argMin3;
const argMax5 = argMax3;

const sin = operation(x => Math.sin(x));
const cos = operation(x => Math.cos(x));

const clamp = operation((x, min, max) => Math.max(min, Math.min(x, max)));
const softClamp = operation((x, min, max, lambda) => min + (max - min) / (1 + Math.exp(lambda * ((max + min) / 2 - x))));
const wrap = operation((x, min, max) => x - Math.floor((x - min) / (max - min)) * (max - min));

const one = cnst(1), two = cnst(2), three = cnst(3);

const operations = {
    "+": [add, 2],
    "-": [subtract, 2],
    "*": [multiply, 2],
    "/": [divide, 2],
    "negate": [negate, 1],
    "argMin3": [argMin3, 3],
    "argMax3": [argMax3, 3],
    "argMin5": [argMin5, 5],
    "argMax5": [argMax5, 5],
    "sin": [sin, 1],
    "cos": [cos, 1],
    "clamp": [clamp, 3],
    "softClamp": [softClamp, 4],
    "wrap": [wrap, 3]};

const variables = { "x": 0, "y": 1, "z": 2 };
const consts = { one, two, three };

const parse = expr => {
    let stack = [];

    for (let sym of expr.trim().split(/\s+/)) {
        if (sym in variables) {
            stack.push(variable(sym));
        } else if (sym in operations) {
            const [op, numArgs] = operations[sym];
            stack.push(op(...stack.splice(-numArgs)));
        } else if (sym in consts) {
            stack.push(consts[sym]);
        } else if (!isNaN(Number(sym))) {
            stack.push(cnst(Number(sym)));
        }
    }

    return stack.pop();
};