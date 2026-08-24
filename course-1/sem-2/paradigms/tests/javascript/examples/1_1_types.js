"use strict";

chapter("Types");
section("Variables are typeless");

let v = 1;
example("v");
example("    typeof(v)");

v = "Hello";
example("v");
example("    typeof(v)");

section("Values are typed");
let as = ["'Hello'", 1, 1.1, true, false, [1, 2, 3], new Array(1, 2, 3), null, undefined];
for (let i = 0; i < as.length; i++) {
    println("v =", as[i]);
    println("    typeof(v) ->", typeof(as[i]));
}

section("Ordinary comparison");
example("'1' == '1'");
example("'1' == 1");
example("'1.0' == 1");
example("undefined == undefined");
example("undefined == null");
example("null == null");
example("0 == []");
example("'10' == [10]");

section("Strict comparison");
example("'1' === '1'");
example("'1' === 1");
example("undefined === undefined");
example("undefined === null");
example("null === null");
example("0 === []");
example("'10' === [10]");

section("Calculations");
subsection("Addition");
example("2 + 3");
example("2.1 + 3.1");
example("'2.1' + '3.1'");
example("'Hello, ' + 'world!'");

subsection("Subtraction");
example("2 - 3");
example("2.1 - 3.1");
example("'2.1' - '3.1'");
example("'Hello, ' - 'world!'");
