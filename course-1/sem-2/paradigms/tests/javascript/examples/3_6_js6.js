"use strict";

chapter("Javascript 6+ features");


section("Template strings");

example("`10 + 20 = ${10 + 20}`");
example("`Random value: ${Math.random()}`");
example("`Hello ${`from ${Math.random()}`.toUpperCase()}`");


section("Sets");

const set = new Set().add(1).add(2).add(1);
example("set");
example("set.size");
example("set.has(1)");
example("set.has(3)");
example("for (const value of set.values()) { println(value); }");


section("Maps");

const mp = new Map().set("hello", 1).set("bye", 2).set("hello", 3);
example("mp");
example("mp.size");
example("mp.has('hello')");
example("mp.get('hello')");
example("mp.get('hello2')");
example("for (const entry of mp.entries()) { println(entry); }");
example("for (const key of mp.keys()) { println(key); }");
example("for (const value of mp.values()) { println(value); }");
example("mp.delete('hello')");
example("mp.get('hello')");
example("mp.has('hello')");


section("Iterators");

subsection("range");
function* range(start, end) {
    while (start < end) {
        yield start;
        start++;
    }
}

example("range(3, 6)");

for (const i of range(3, 6)) {
    println(`\ti = ${i}`)
}

println("Same as");

for (let it = range(3, 6), next = it.next(); !next.done; next = it.next()) {
    println(`\ti = ${next.value}`)
}


subsection("Fibonacci numbers");

function* fibs(a = 1, b = 1) {
    while (true) {
        yield a;
        [a, b] = [b, a + b]
    }
}

example("fibs()");
for (const i of fibs()) {
    println(i);
    if (i === 13) {
        break;
    }
}


subsection("Coroutines");

function* adderGen(step) {
    let value = 0;
    while (true) {
        value += yield value + step;
    }
}

const addGen = adderGen(1000);
for (let i = 0; i <10; i++) {
    println(addGen.next(i).value);
}
