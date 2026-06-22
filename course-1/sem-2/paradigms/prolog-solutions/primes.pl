init(_).
prime(N) :- N > 1, \+ composite(N).
composite(N) :- N > 1, is_composite(N, 2).
prime_divisors(N, Divisors) :- is_prime_divisor(N, Divisors, 2).
square_divisors(N, SquareDivisors) :- prime_divisors(N, Divisors), duplicate(Divisors, SquareDivisors).

is_composite(N, I) :- I * I =< N, (0 =:= N mod I; I1 is I + 1, is_composite(N, I1)).

is_prime_divisor(1, [], _) :- !.
is_prime_divisor(N, [N], I) :- I * I > N, !.
is_prime_divisor(N, Divisors, I) :- (N > 1, I * I =< N, (
        0 =:= N mod I, Divisors = [I|Rest], N1 is N // I, is_prime_divisor(N1, Rest, I);
        0 =\= N mod I, I1 is I + 1, is_prime_divisor(N, Divisors, I1)
    )).

duplicate([], []).
duplicate([Divisor|Rest], [Divisor, Divisor|SquareRest]) :- duplicate(Rest, SquareRest).
