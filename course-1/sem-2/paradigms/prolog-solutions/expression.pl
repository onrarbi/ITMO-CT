:- load_library('alice.tuprolog.lib.DCGLibrary').

lookup(K, [(K, V) | _], V) :- !.
lookup(K, [_ | T], V) :- lookup(K, T, V).

variable(Name, variable(Name)).
const(Value, const(Value)).

operation(op_add, A, B, R) :- R is A + B.
operation(op_subtract, A, B, R) :- R is A - B.
operation(op_multiply, A, B, R) :- R is A * B.
operation(op_divide, A, B, R) :- R is A / B.
operation(op_negate, A, R) :- R is -A.

evaluate(const(Value), _, Value) :- !.
evaluate(variable(Name), Vars, R) :- lookup(Name, Vars, R), !.
evaluate(operation(Op, A), Vars, R) :- evaluate(A, Vars, AV), operation(Op, AV, R), !.
evaluate(operation(Op, A, B), Vars, R) :- evaluate(A, Vars, AV), evaluate(B, Vars, BV), operation(Op, AV, BV, R), !.

postfix_str(E, Atom) :- ground(E), phrase(to_postfix(E), Chars), atom_chars(Atom, Chars), !.
postfix_str(E, Atom) :- atom(Atom), atom_chars(Atom, Chars), phrase(postfix(E), Chars), !.

postfix(E) --> spaces, expression(E), spaces.

to_postfix(const(Value)) --> { number_chars(Value, Chars) }, Chars.
to_postfix(variable(Name)) --> { atom_chars(Name, Chars) }, Chars.
to_postfix(operation(Op, A)) --> ['('], to_postfix(A), [' '], unary_operation(Op), [')'].
to_postfix(operation(Op, A, B)) --> ['('], to_postfix(A), [' '], to_postfix(B), [' '], binary_operation(Op), [')'].

expression(const(Value)) --> number_token(Chars), { number_chars(Value, Chars) }.
expression(variable(Name)) --> variable_token(Chars), { atom_chars(Name, Chars) }.
expression(operation(Op, A)) --> ['('], spaces, expression(A), spaces, unary_operation(Op), spaces, [')'].
expression(operation(Op, A, B)) --> ['('], spaces, expression(A), spaces, expression(B), spaces, binary_operation(Op), spaces, [')'].

binary_name(op_add, '+').
binary_name(op_subtract, '-').
binary_name(op_multiply, '*').
binary_name(op_divide, '/').

unary_name(op_negate, 'negate').

binary_operation(Op) --> { binary_name(Op, Name), atom_chars(Name, Chars) }, Chars.

unary_operation(Op) --> { unary_name(Op, Name), atom_chars(Name, Chars) }, Chars.

spaces --> [' '], spaces.
spaces --> ['\t'], spaces.
spaces --> ['\n'], spaces.
spaces --> ['\r'], spaces.
spaces --> [].

variable_token([C]) --> { member(C, [x, y, z]) }, [C].

digit(D) --> { member(D, ['0', '1', '2', '3', '4', '5', '6', '7', '8', '9']) }, [D].
digits([D]) --> digit(D).
digits([D | Rest]) --> digit(D), digits(Rest).

fraction(['.' | Rest]) --> ['.'], digits(Rest).
fraction([]) --> [].

positive_number(Chars) --> digits(Int), fraction(Frac), { append(Int, Frac, Chars) }.

number_token(['-' | Rest]) --> ['-'], positive_number(Rest).
number_token(Chars) --> positive_number(Chars).