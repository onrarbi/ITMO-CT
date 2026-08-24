%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Арифметические выражения %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%


%%% Конструкторы

variable(Name, variable(Name)).
const(Value, const(Value)).

add(A, B, bin(add, A, B)).
sub(A, B, bin(sub, A, B)).
mul(A, B, bin(mul, A, B)).
div(A, B, bin(div, A, B)).

example(E) :-
    variable(x, Vx), variable(y, Vy), variable(z, Vz), const(100, C),
    sub(Vy, Vz, Syz), mul(Vx, Syz, MxSyz), add(MxSyz, C, E).
/*
?- example(E).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
*/


%%% Вычисление выражений

% Реализация бинарных операций

bin(add, A, B, R) :- R is A + B.
bin(sub, A, B, R) :- R is A - B.
bin(mul, A, B, R) :- R is A * B.
bin(div, A, B, R) :- R is A / B.

% Из lists.pl
lookup(K, [(K, V) | _], V).
lookup(K, [_ | T], V) :- lookup(K, T, V).

% Вычисление выражений

eval(const(Value), _, Value).
eval(variable(Name), Vars, R) :- lookup(Name, Vars, R).
eval(bin(Op, A, B), Vars, R) :-
    eval(A, Vars, AV),
    eval(B, Vars, BV),
    bin(Op, AV, BV, R).

/*
?- example(E), eval(E, [(x, 1), (y, 2), (z, 3)], R).
   R / 99
*/


%%% Упрощение выражений

% Упрощение бинарных операций

simplify_bin(Op, const(A), const(B), const(R)) :- !,
    eval(bin(Op, const(A), const(B)), _, R).
simplify_bin(add, A, const(0), A).
simplify_bin(add, const(0), B, B).
simplify_bin(sub, A, const(0), A).
simplify_bin(mul, A, const(1), A).
simplify_bin(mul, const(1), B, B).
simplify_bin(mul, _, const(0), const(0)).
simplify_bin(mul, const(0), _, const(0)).
simplify_bin(div, A, const(1), A).
simplify_bin(div, const(0), _, const(0)).

simplify_bin(bin(Op, A, B), R) :- simplify_bin(Op, A, B, R), !.
simplify_bin(E, E).


/*
?- simplify_bin(bin(add,const(1), const(2)), R).
   const(3)
?- example(E), simplify_bin(E, R), E = R.
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   R / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
?- example(E), add(const(0), E, EE), simplify_bin(EE, R), E=R.
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   EE / bin(add,const(0),bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100)))
   R / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
?- example(E), mul(const(0), E, EE), simplify_bin(EE, R).
   E / bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100))
   EE / bin(mul,const(0),bin(add,bin(mul,variable(x),bin(sub,variable(y),variable(z))),const(100)))
   R / const(0)
*/


% Рекурсивное упрощение выражений

simplify(bin(Op, A, B), R) :- !,
        simplify(A, AS),
        simplify(B, BS),
        simplify_bin(bin(Op, AS, BS), R).
simplify(E, E).

% (2 * x - 3)'
d_example(
    bin(sub,
        bin(add,
            bin(mul, const(0), variable(x)),
            bin(mul, const(2), const(1))
        ),
        const(0)
    )
).

/*
?- d_example(E), simplify_bin(E, R).
   R / bin(add,bin(mul,const(0),variable(x)),bin(mul,const(2),const(1)))
?- d_example(E), simplify(E, R).
   R / const(2)
*/


%%% Вычисление производных

% Производные бинарных операций

diff_bin(add, A, B, DA, DB, bin(add, DA, DB)).
diff_bin(sub, A, B, DA, DB, bin(sub, DA, DB)).
diff_bin(mul, A, B, DA, DB, bin(add,
    bin(mul, DA, B),
    bin(mul, A, DB)
)).
diff_bin(div, A, B, DA, DB, bin(div,
    bin(sub, bin(mul, DA, B), bin(mul, A, DB)),
    bin(mul, B, B)
)).


% Производные выражений

diff(const(_), _, const(0)).
diff(variable(Name), Name, const(1)) :- !.
diff(variable(_), _, const(0)).
diff(bin(Op, A, B), V, R) :-
    diff(A, V, DA),
    diff(B, V, DB),
    diff_bin(Op, A, B, DA, DB, R).

diff_example(
    bin(sub,
        bin(mul, const(2), variable(x)),
        const(3)
    )
).

/*
?- diff(const(10), _, R).
   R / const(0)
?- diff(variable(x), x, R).
   R / const(1)
?- diff(variable(x), y, R).
   R / const(0)
?- diff(bin(mul,const(2),variable(x)), x, R).
   R / bin(add,bin(mul,const(0),variable(x)),bin(mul,const(2),const(1)))
?- diff_example(E), diff(E, x, DE).
   E / bin(sub,bin(mul,const(2),variable(x)),const(3))
   DE / bin(sub,bin(add,bin(mul,const(0),variable(x)),bin(mul,const(2),const(1))),const(0))
?- diff_example(E), diff(E, x, DE), d_example(DE).
   yes
?- diff_example(E), diff(E, x, DE), simplify(DE, SDE).
   E / bin(sub,bin(mul,const(2),variable(x)),const(3))
   DE / bin(sub,bin(add,bin(mul,const(0),variable(x)),bin(mul,const(2),const(1))),const(0))
   SDE / const(2)
*/
