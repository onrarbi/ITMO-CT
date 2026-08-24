"use strict";

chapter("Arrays");

section("Like in Java?");
example("as = [10, 20, 30]");
println("as -> [" + as +"]");
example("as.length");
example("as[2]");
example("as[3]");

subsection("Mostly");
example("as['2']");
example("as[2.0]");
example("as['2.0']");
example("as.constructor.name");

section("Variable length");
subsection("push/pop");
example("as = new Array(10, 20, 30)");
example("as.push(40, 50)");
dumpArray(as);
example("as.pop()");
dumpArray(as);
example("as.pop()");
dumpArray(as);

subsection("unshift/shift");
example("as.unshift(60, 70)");
dumpArray(as);
example("as.shift()");
dumpArray(as);
example("as.shift()");
dumpArray(as);


section("Weird indices");
example("as[3] = 80");
dumpArray(as);
example("as[10] = 90");
dumpArray(as);
example("    typeof(as[5])");

example("as[-1] = 100");
dumpArray(as);
example("   as[-1]");

example("as['2.0'] = 110");
dumpArray(as);
example("    as['2.0']");

example("as['hello'] = 120");
dumpArray(as);
example("    as['hello']");


section("Enumeration")

print("Indexed for")
for (var i = 0; i < as.length; i++) {
    example("    as[i]");
}


print("for of")
for (var a of as) {
    example("    a");
}
