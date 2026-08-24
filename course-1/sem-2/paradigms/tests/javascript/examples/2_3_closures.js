"use strict";

chapter("Closures");
section("Functions with state");

function incrementor(step) {
    let n = 0;
    return function() {
        n += step;
        return n;
    }
}

let inc = incrementor(10);
println("n and step are captured in closure");
println("    inc() = " + inc());
println("    inc() = " + inc());
println("    inc() = " + inc());


let inc2 = incrementor(5);
println("Each incrementor has its own state");
println("    inc() = " + inc() + " " + inc2());
println("    inc() = " + inc() + " " + inc2());
println("    inc() = " + inc() + " " + inc2());

section("Be careful");

println("addersVar shares same var i:");
function addersVar(n) {
    let a = [];
    let i; // Declared before loop
    for (i = 0; i < n; i++) {
        a.push(v => i + v);
    }
    println("i = " + i);
    return a;
}

let adders = addersVar(3);
for (let j = 0; j < a.length; j++) {
    println("    addersVar[" + j + "] adds " + adders[j](0));
}


subsection("Intermediate function trick");

println("addersFun has a copy of var i named w:");
function addersFun(n) {
    let a = [];
    let i; // Declared before loop
    for (i = 0; i < n; i++) {
        // Call of declared intermediate function
        a.push((w => v => w + v)(i));
    }
    return a;
}

const adder = addersFun(3);
for (let j = 0; j < a.length; j++) {
    println("    addersFun[" + j + "] adds " + adder[j](0));
}
println();


subsection("Use let");

println("No issues with let in loop:");
function addersLet(n) {
    let a = [];
    for (let i = 0; i < n; i++) {
        a.push(v => i + v);
    }
    return a;
}

a = addersLet(3);
for (let j = 0; j < a.length; j++) {
    println("    addersLet[" + j + "] adds " + a[j](0));
}


section("Common shared state");
function PrivatePoint(x, y) {
    println("Constructor called");
    let z = 0;
    this.getX = function() { return x; };
    this.setX = function(value) { x = value; };
    this.getY = function() { return y; };
    this.setY = function(value) { y = value; };
    this.getZ = function() { return z; };
    this.setZ = function(value) { z = value; };
}

let privatePoint = new PrivatePoint(10, 20);
dumpObject("privatePoint", privatePoint);

privatePoint.setX(100);
privatePoint.setZ(1000);
dumpObject("Modified privatePoint", privatePoint);
