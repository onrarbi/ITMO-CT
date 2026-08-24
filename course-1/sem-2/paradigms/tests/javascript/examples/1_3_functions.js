"use strict";

chapter("Functions");
section("Arguments");

subsection("Indices");
let dumpArgs = function() {
    println(arguments.constructor.name);
    for (let i = 0; i < arguments.length; i++) {
        println("   ", i, arguments[i]);
    }
};
println("let dumpArgs =", dumpArgs);
example("dumpArgs(1, 2, 'hello', null, undefined)");


subsection("Values");
let dumpArgs2 = function() {
    println(arguments.constructor.name);
    for (const arg of arguments) {
        println("   ", arg);
    }
};
println("let dumpArgs2 =", dumpArgs2);
example("dumpArgs2(1, 2, 'hello', null, undefined)");


subsection("sum");

let sum = function() {
    let result = 0;
    for (const arg of arguments) {
        result += arg;
    }
    return result;
};
println("let sum =", sum);
example("sum(1, 2, 3)");


subsection("minimum");

let minimum = function() {
    let result = Infinity;
    for (const arg of arguments) {
        if (result > arg) {
            result = arg;
        }
    }
    return result;
};
println("let minimum =", minimum);
example("minimum(1, -2, 3)");


section("Named functions and arguments");

function min(a, b) {
    //println("   ", typeof(a), typeof(b));
    return a < b ? a : b;
}
println(min);
example("min(1, -1)");
example("min(1, -1)");
example("min(1)");
example("min()");

subsection("Still values");
let m = min;
example("m");
example("m.name");
example("m.length");
example("m(10, 20)");


section("Default arguments");

function def(a = -10, b = -20) {
    return [a, b];
}
println(def);
example("def(1, 2)");
example("def(1)");
example("def()");


section("Rest argument and spread calls");

function minRest(first, ...rest) {
    let result = first;
    for (const a of rest) {
        result = min(result, a);
    }
    return result;
}
println(minRest);
example("minRest(1)");
example("minRest(1, -1)");
example("minRest(1, -1, 2, -2)");
example("minRest(...[1, -1, 2, -2])");
example("minRest(1, -1, ...[2, -2])");


section("Arrow functions");

const minArr = (a, b) => a < b ? a : b;
example("minArr");
example("minArr(1, -2)");

const minArrow = (first, ...rest) => {
    let result = first;
    for (const a of rest) {
        result = Math.min(result, a);
    }
    return result;
};
example("minArrow");
example("minArrow(1)");
example("minArrow(1, -1)");
example("minArrow(1, -1, 2, -2)");

const stupidArrow = (v) => {
    println(v);
    // No "arguments" for arrow functions
    // println(arguments);
};
example("stupidArrow");
example("stupidArrow(3)");
