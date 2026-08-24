%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Задача о расстановке ферзей %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

% https://ru.wikipedia.org/wiki/Задача_о_восьми_ферзях


%%% Вспомогательные правила

% Из lists.pl
range(L, L, []).
range(N, L, [N | T]) :- N < L, N1 is N + 1, range(N1, L, T).
/*
?- range(0, 10, R).
   R / [0,1,2,3,4,5,6,7,8,9]
*/

zip([], _, []) :- !.
zip(_, [], []) :- !.
zip([H1 | T1], [H2 | T2], [(H1, H2) | TR]) :- zip(T1, T2, TR).
/*
?- zip([x, y, z], [1, 2, 3], R).
   R / [(x,1),(y,2),(z,3)]
*/

% Из high-order.pl
map([], _, []).
map([H | T], F, [RH | RT]) :- G =.. [F, H, RH], call(G), map(T, F, RT).

% Все элементы различны
distinct([]).
distinct([H | T]) :- \+ member(H, T), distinct(T).
/*
?- distinct([1, 2, 3]).
   yes.
?- distinct([1, 2, 3, 2]).
   no.
*/

% Все элементы первого списка входят во второй
all_members([], _).
all_members([H | T], L) :- member(H, L), all_members(T, L).
/*
?- all_members([1, 3], [1, 2, 3]).
   yes.
?- all_members([1, 4], [1, 2, 3]).
   no.
*/


%%% Наивное решение

% Номер диагонали
diag1((R, C), D) :- D is R - C.
diag2((R, C), D) :- D is R + C.
/*
?- map([(1, 1), (1, 2), (2, 1), (2, 2)], diag1, R).
   R / [0,-1,1,0]
?- map([(1, 1), (1, 2), (2, 1), (2, 2)], diag2, R).
   R / [2,3,3,4]
*/

queens(N, Rows) :-
    length(Rows, N),
    range(0, N, Range), all_members(Rows, Range),
    distinct(Rows),
    zip(Rows, Range, Queens),
    map(Queens, diag1, Diag1), distinct(Diag1),
    map(Queens, diag2, Diag2), distinct(Diag2).

/*
?- queens(4, R).
   R / [1,3,0,2]
   R / [2,0,3,1]
?- queens(5, R).
   R / [0,2,4,1,3]
   R / [0,3,1,4,2]
   R / [1,3,0,2,4]
   R / [1,4,2,0,3]
   R / [2,0,3,1,4]
   R / [2,4,1,3,0]
   R / [3,0,2,4,1]
   R / [3,1,4,2,0]
   R / [4,1,3,0,2]
   R / [4,2,0,3,1]
?- queens(7, R).
   R / [0,2,4,6,1,3,5]
   R / [0,3,6,2,5,1,4]
   R / [0,4,1,5,2,6,3]
   R / [0,5,3,1,6,4,2]
   R / [1,3,0,6,4,2,5]
   R / [1,3,5,0,2,4,6]
   ...
*/


%%% Быстрое решение
% (рекурсия по столбцам)

%fast_queens(Range, C, Rows, Diag1, Diag2)
fast_queens(_, 0, [], [], []) :- !.
fast_queens(Range, C, [R | Rows], [D1 | Diag1], [D2 | Diag2]) :-
    C1 is C - 1,
    fast_queens(Range, C1, Rows, Diag1, Diag2),
    member(R, Range), \+ member(R, Rows),
    D1 is R - C, \+ member(D1, Diag1),
    D2 is R + C, \+ member(D2, Diag2).

fast_queens(N, R) :- range(0, N, Range), fast_queens(Range, N, R, _, _).

/*
?- fast_queens(8, R).
   R / [3,1,6,2,5,7,4,0]
   ... 91 more solutions
?- bagof(R, fast_queens(8, R), _RS), length(_RS, N).
   N / 92
?- queens(10, R).
   R / [6,3,1,8,4,9,7,5,2,0]
   ... 723 more solutions
?- bagof(R, queens(6, R), _RS), length(_RS, N).
   N / 4
*/
