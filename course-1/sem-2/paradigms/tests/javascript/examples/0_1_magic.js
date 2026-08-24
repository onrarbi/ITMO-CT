"use strict";

// Magic helper functions
function example(s, description) {
    const result = (() => {
        try {
            return eval(s);
        } catch (e) {
            return e;
        }
    })();

    if (description) {
        println(description + ":", s, "->", result);
    } else {
        println(s, "->", result);
    }
}

function examples(collection, template) {
    collection.forEach(function(name) {
        return example(template.replace('#', name).replace('#', name));
    });
}

function subsection(name) {
    println();
    println("---", name);
}

function section(name) {
    println();
    println();
    println("===", name, "===");
}

function chapter(name) {
    println();
    println();
    println();
    println("##########", name, "##########");
}

function lecture(name) {
    println();
    println("#".repeat(name.length + 16));
    println("### Lecture " + name + " ###");
    println("#".repeat(name.length + 16));
}

// Helper function
function dumpProperty(o, property) {
    if (typeof(o[property]) === "function") {
        if (o[property].length === 0) {
            println("    " + property.toString() + "() -> " + o[property]());
        } else {
            println("    " + property.toString() + "(...)");
        }
    } else {
        println("    " + property.toString() + " = " + o[property]);
    }
}

function dumpObject(name, o) {
    println(name + ": " + o.constructor.name);
    for (const property in o) {
        dumpProperty(o, property);
    }
    let symbols = Object.getOwnPropertySymbols(o);
    if (symbols.length > 0) {
        for (const property of symbols) {
            dumpProperty(o, property);
        }
    }
}

function dumpArray(a) {
    const other = (Object.keys(a)
        .filter(i => i != "" + parseInt(i) || !(0 <= i && i < a.length))
        .map(name => name + " = " + a[name])
        .join(", ")
    );
    println("    length: " + a.length + ", elements: [" + a + "]" + (other ? ", other: {" + other + "}" : ""));
}

if (!String.prototype.repeat) {
    String.prototype.repeat = function(count) {
        let result = "";
        for (let i = 0; i < count; i++) {
            result += this;
        }
        return result;
    }
}
