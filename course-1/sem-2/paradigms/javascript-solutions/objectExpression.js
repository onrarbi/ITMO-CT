"use strict";

function Operation(operator, action, ...args) {
    this.operator = operator;
    this.action = action;
    this.args = args;
}

Operation.prototype.evaluate = function (...vals) {
    return this.action(...this.args.map(arg => arg.evaluate(...vals)));
};

Operation.prototype.toString = function () {
    return this.args.map(arg => arg.toString()).join(" ") + " " + this.operator;
};

Operation.prototype.prefix = function () {
    return "(" + this.operator + " " + this.args.map(arg => arg.prefix()).join(" ") + ")";
};

function Const(value) {
    this.value = value;
}

Const.prototype.evaluate = function () {
    return this.value;
};

Const.prototype.toString = function () {
    return this.value.toString();
};

Const.prototype.prefix = Const.prototype.toString;

function Variable(value) {
    this.value = value;
}

Variable.prototype.evaluate = function (...args) {
    return args[variables.get(this.value)];
};

Variable.prototype.toString = function () {
    return this.value;
};

Variable.prototype.prefix = Variable.prototype.toString;

const createOperation = (operator, action) => {
    const Op = function (...args) {
        Operation.call(this, operator, action, ...args)
    };
    Op.prototype = Object.create(Operation.prototype);
    Op.prototype.constructor = Op;
    return Op;
};

const Add = createOperation("+", (a, b) => a + b);
const Subtract = createOperation("-", (a, b) => a - b);
const Multiply = createOperation("*", (a, b) => a * b);
const Divide = createOperation("/", (a, b) => a / b);
const Negate = createOperation("negate", a => -a);

const Wrap = createOperation("wrap", (x, min, max) => x - Math.floor((x - min) / (max - min)) * (max - min));
const ArcTan12 = createOperation("atan12", (...args) => args.length === 1 ? Math.atan(args[0]) : Math.atan2(args[0], args[1]));

const variables = new Map ([
    ["x", 0], 
    ["y", 1], 
    ["z", 2]
]);

const operations = new Map ([
    ["+", [Add, 2, 2]],
    ["-", [Subtract, 2, 2]],
    ["*", [Multiply, 2, 2]],
    ["/", [Divide, 2, 2]],
    ["negate", [Negate, 1, 1]],
    ["wrap", [Wrap, 3, 3]],
    ["atan12", [ArcTan12, 1, 2]],
]);

const numbers = /^-?\d+$/;

const openingBrackets = new Map ([
    ["(", ")"],
    ["[", "]"],
    ["{", "}"],
    ["<", ">"],
    ["«", "»"],
]);

const closingBrackets = new Set([...openingBrackets.values()]);

function ParseError(message, position) {
    this.message = message + " at position " + (position + 1);
}

ParseError.prototype = Object.create(Error.prototype);
ParseError.prototype.constructor = ParseError;
ParseError.prototype.name = "ParseError";

function createError(name) {
    function CustomError(message, position) {
        ParseError.call(this, message, position);
    }

    CustomError.prototype = Object.create(ParseError.prototype);
    CustomError.prototype.constructor = CustomError;
    CustomError.prototype.name = name;

    return CustomError;
}

const OperationError = createError("OperationError");
const BracketError = createError("BracketError");
const ArgumentError = createError("ArgumentError");

function Parser(expr, pos) {
    this.expr = expr;
    this.pos = 0;
}

Parser.prototype.skipWhitespaces = function() {
    while (this.pos < this.expr.length && /\s/.test(this.expr[this.pos])) {
        this.pos++;
    }
};

Parser.prototype.readNext = function() {
    const startPos = this.pos;

    while (this.pos < this.expr.length && !/\s/.test(this.expr[this.pos]) && this.expr[this.pos] && !openingBrackets.has(this.expr[this.pos]) && !closingBrackets.has(this.expr[this.pos])) {
        this.pos++;
    }

    return this.expr.slice(startPos, this.pos);
};

Parser.prototype.parseOperand = function() {
    const symPos = this.pos;
    const sym = this.readNext();

    if (variables.has(sym)) {
        return new Variable(sym);
    } else if (numbers.test(sym)) {
        return new Const(parseInt(sym));
    } else if (operations.has(sym)) {
        throw new OperationError("Operation '" + sym + "' is outside of brackets", symPos);
    }

    throw new ArgumentError("Unknown character '" + sym + "'", symPos);
}

Parser.prototype.parseInBrackets = function() {
    const opBr = this.expr[this.pos];
    const clBr = openingBrackets.get(opBr);
    this.pos++;
    this.skipWhitespaces();
    const opPos = this.pos;
    const sym = this.readNext();

    if (sym.length === 0) {
        throw new OperationError("Operation expected after opening bracket", opPos);
    }

    if (!operations.has(sym)) {
        throw new OperationError("Unknown operation '" + sym +"'", opPos);
    }

    const [Op, minArgs, maxArgs] = operations.get(sym);
    const args = [];

    while (true) {
        this.skipWhitespaces();

        if (this.pos >= this.expr.length) {
            throw new BracketError("Unexpected end of expression: expected '" + clBr + "'", this.pos);
        }

        if (closingBrackets.has(this.expr[this.pos])) {
            if (this.expr[this.pos] !== clBr) {
                throw new BracketError("Mismatching bracket: expected '" + clBr + "', but got '" + this.expr[this.pos] + "'", this.pos);
            }

            this.pos++;
            break;
        }

        args.push(this.parseExpr());
    }

    if (args.length < minArgs || args.length > maxArgs) {
        if (minArgs === maxArgs) {
            throw new ArgumentError("Operation '" + sym + "' requires " + minArgs + " arguments, but got " + args.length, opPos);
        }

        throw new ArgumentError("Operation '" + sym + "' requires from " + minArgs + " to " + maxArgs + " arguments, but got " + args.length, opPos);
    }

    return new Op(...args);
};

Parser.prototype.parseExpr = function() {
    this.skipWhitespaces();

    if (this.pos >= this.expr.length) {
        throw new ParseError("End of expression", this.pos);
    }

    const ch = this.expr[this.pos];
    if (closingBrackets.has(ch)) {
        throw new BracketError("Unexpected closing bracket", this.pos);
    }

    if (!openingBrackets.has(ch)) {
        return this.parseOperand();
    }

    return this.parseInBrackets();
};

function parsePrefix(expr) {
    const parser = new Parser(expr);
    const result = parser.parseExpr();

    parser.skipWhitespaces();

    if (parser.pos !== expr.length) {
        throw new ParseError("Unexpected characters after expression", parser.pos);
    }

    return result
}

function parse(expr) {
    let stack = [];

    for (let sym of expr.trim().split(/\s+/)) {
        if (variables.has(sym)) {
            stack.push(new Variable(sym));
        } else if (operations.has(sym)) {
            const [op, numArgs] = operations.get(sym);
            stack.push(new op(...stack.splice(-numArgs)));
        } else if (numbers.test(sym)) {
            stack.push(new Const(parseInt(sym)));
        }
    }

    return stack.pop();
}
