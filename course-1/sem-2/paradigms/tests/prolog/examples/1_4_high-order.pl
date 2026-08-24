%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Правила высшего порядка %
%%%%%%%%%%%%%%%%%%%%%%%%%%%

% Отображение

map([], _, []).
map([H | T], F, [RH | RT]) :- G =.. [F, H, RH], call(G), map(T, F, RT).

% From calc.pl
inc(N, R) :- number(N), !, R is N + 1.
inc(N, R) :- number(R), !, N is R - 1.

/*
?- map([10, 20, 30], inc, R).
   R / [11,21,31]
?- map(R, inc, [10, 20, 30]).
   R / [9,19,29]
*/


% Продвинутый call

call(F, Bs) :-
   F =.. As,
   append(As, Bs, Cs),
   G =.. Cs,
   call(G).

map2([], _, []).
map2([H | T], F, [RH | RT]) :- call(F, [H, RH]), map2(T, F, RT).

/*
?- map2([10, 20, 30], inc, R).
   R / [11,21,31]
?- map2(R, inc, [10, 20, 30]).
   R / [9,19,29]
*/

add(A, B, R) :- R is A + B.
/*
?- map2(R, add(10), [10, 20, 30]).
   R / [20,30,40]
*/

% Фильтрация

filter([], _, []).
filter([H | T], P, [H | RT]) :- call(P, [H]), !, filter(T, P, RT).
filter([_ | T], P, RT) :- filter(T, P, RT).

odd(N) :- 1 is mod(N, 2).

/*
?- filter([1, 2, 3], odd, R).
   R / [1,3]
*/


% Левая свертка

foldLeft([], V, _, V).
foldLeft([H | T], V, F, R) :- call(F, [V, H, RH]), foldLeft(T, RH, F, R).

/*
?- foldLeft([1, 2, 3], 0, add, R).
   R / 6
*/


% Транспонирование матрицы

transpose([[] | _], []) :- !.
transpose(M, [RH | RT]) :-
    map(M, head, RH),
    map(M, tail, Tails),
    transpose(Tails, RT).

head([H | _], H).
tail([_ | T], T).

/*
?- transpose([[1, 2], [3, 4], [5, 6]], R)
   R / [[1,3,5],[2,4,6]]
*/
