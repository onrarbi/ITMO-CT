"use strict";

chapter("Methods and classes");

section("Advanced properties");

let hello = "world";
let propertiesExample = {
    // Shorthand property
    hello,
    // Calculated property
    [hello + hello]: hello,
    ...point
}

dumpObject("example", propertiesExample);


section("Binding parameters");

subsection("Arrays");

function withArray([x, y, ...rest]) {
    println("rest =", rest);
    return x * y;
}

example("withArray");
example("withArray([10, 20, 30])");
println();

function vScalar([x1, y1], [x2, y2]) {
    return x1 * x2 + y1 * y2;
}

example("vScalar");
example("vScalar([10, 20], [30, 40])");
println();


subsection("Objects");

function withObject({x, y, ...rest}) {
    dumpObject("rest", rest);
    return x * y;
}

example("withObject");
example("point = {x: 10, y: 20, z: 30};");
example("withObject(point)");
println();

example("withObject({x: 100, y: 200})");
println();


section("Method properties");

point = {
    x: 10,
    y: 20,
    getX() { return this.x; },
    setX(x) { this.x = x; },
    getY() { return this.y; },
    setY(y) { this.y = y; }
};
dumpObject("point", point);
example("point.getX()");
example("point.setX(100)");
example("point.getX()");

println();
println("Same as");
point = {
    x: 10,
    y: 20,
    getX: function() { return this.x; },
    setX: function(x) { this.x = x; },
    getY: function() { return this.y; },
    setY: function(y) { this.y = y; }
};

dumpObject("point", point);
example("point.getX()");
example("point.setX(100)");
example("point.getX()");


section("Getters and setters");

point = {
    _x: 10,
    _y: 20,
    get x() {
        println("get x =", this._x);
        return this._x;
    },
    set x(x) {
        println("set x =", x);
        this._x = x;
    },
    get y() {
        println("get y = ", this._y);
        return this._y;
    }
};

example("point.x");
example("point.x = 20");
example("point.x");
example("point.y");
example("point.y = 20");


section("Classes");

class CPoint {
    constructor(x, y) {
        this.x = x;
        this.y = y;
    }
    getX() {
        return this.x;
    }
    setX(x) {
        this.x = x;
    }
    getY() {
        return this.y;
    }
    setY(y) {
        this.y = y;
    }
    toString() {
        return `${this.constructor.name}(${this.x}, ${this.y})`;
    }
    static ORIGIN = new CPoint(0, 0);
    static origin() {
        return CPoint.ORIGIN;
    }
}

example("CPoint");
example("point = new CPoint(10, 20)");
dumpObject("point", point);
example("point.getX()");
example("point.setX(100)");
example("point.getX()");
example("CPoint.ORIGIN");
example("CPoint.origin()");

println();
println("Same as");

function UPoint(x, y) {
    this.x = x;
    this.y = y;
}
UPoint.prototype.getX = function() { return this.x; };
UPoint.prototype.setX = function(x) { this.x = x; };
UPoint.prototype.getY = function() { return this.y; };
UPoint.prototype.setY = function(y) { this.y = y; };
UPoint.prototype.toString = function() { return `${this.constructor.name}(${this.x}, ${this.y})`; };

UPoint.ORIGIN = new UPoint(0, 0);
UPoint.origin = function () { return UPoint.ORIGIN; };

example("point = new UPoint(10, 20)");
dumpObject("point", point);
example("point.getX()");
example("point.setX(100)");
example("point.getX()");
example("UPoint.ORIGIN");
example("UPoint.origin()");


section("Inheritance");

class CShiftedPoint extends CPoint {
    constructor(x, y, dx, dy) {
        super(x, y);
        this.dx = dx;
        this.dy = dy;
    }
    getX() {
        return super.getX() + this.dx;
    }
    getY() {
        return super.getY() + this.dy;
    }
    toString() {
        return `${this.constructor.name}(${this.x}, ${this.y}, ${this.dx}, ${this.dy})`;
    }
}

example("point = new CShiftedPoint(10, 20, 1, 2)");
dumpObject("point", point);
example("point.getX()");
example("point.setX(100)");
example("point.getX()");

println();
println("Same as");

function UShiftedPoint(x, y, dx, dy) {
    UPoint.call(this, x, y);
    this.dx = dx;
    this.dy = dy;
}
UShiftedPoint.prototype = Object.create(UPoint.prototype);
UShiftedPoint.prototype.constructor = UShiftedPoint;
UShiftedPoint.prototype.getX = function() { return UPoint.prototype.getX.call(this) + this.dx; },
UShiftedPoint.prototype.getY = function() { return UPoint.prototype.getY.call(this) + this.dy; },
UPoint.prototype.toString = function() { return `${this.constructor.name}(${this.x}, ${this.y}, ${this.dx}, ${this.dy})`; };

example("point = new UShiftedPoint(10, 20, 1, 2)");
dumpObject("point", point);
example("point.getX()");
example("point.setX(100)");
example("point.getX()");
