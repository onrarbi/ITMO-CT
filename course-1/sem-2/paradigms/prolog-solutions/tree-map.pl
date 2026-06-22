map_build(ListMap, TreeMap) :- length(ListMap, Length), build_tree(Length, ListMap, TreeMap, []), !.

build_tree(0, List, empty, List) :- !.
build_tree(Length, List, node(Key, Value, Left, Right), Rest) :-
    Length > 0, LeftLength is Length // 2, RightLength is Length - LeftLength - 1,
    build_tree(LeftLength, List, Left, [(Key, Value) | AfterRoot]), build_tree(RightLength, AfterRoot, Right, Rest), !.

map_get(node(Key, Value, _, _), Key, Value) :- !.
map_get(node(NodeKey, _, Left, _), Key, Value) :- Key < NodeKey, !, map_get(Left, Key, Value).
map_get(node(NodeKey, _, _, Right), Key, Value) :- Key > NodeKey, !, map_get(Right, Key, Value).

map_remove(empty, _, empty) :- !.
map_remove(node(Key, _, Left, Right), Key, Result) :- !, merge(Left, Right, Result).
map_remove(node(NodeKey, NodeValue, Left, Right), Key, node(NodeKey, NodeValue, NewLeft, Right)) :- Key < NodeKey, !, map_remove(Left, Key, NewLeft).
map_remove(node(NodeKey, NodeValue, Left, Right), Key, node(NodeKey, NodeValue, Left, NewRight)) :- Key > NodeKey, !, map_remove(Right, Key, NewRight).

merge(empty, Tree, Tree) :- !.
merge(Tree, empty, Tree) :- !.
merge(Left, Right, node(Key, Value, Left, NewRight)) :- get_min(Right, Key, Value, NewRight), !.

get_min(node(Key, Value, empty, Right), Key, Value, Right) :- !.
get_min(node(NodeKey, NodeValue, Left, Right), Key, Value, node(NodeKey, NodeValue, NewLeft, Right)) :- get_min(Left, Key, Value, NewLeft), !.

lower_entry(empty, _, Best, Best) :- !.
lower_entry(node(NodeKey, NodeValue, _, Right), Key, Best, Entry) :- NodeKey < Key, !, lower_entry(Right, Key, (NodeKey, NodeValue), Entry).
lower_entry(node(NodeKey, _, Left, _), Key, Best, Entry) :- Key =< NodeKey, !, lower_entry(Left, Key, Best, Entry).

map_lowerKey(Map, Key, LowerKey) :- map_lowerEntry(Map, Key, (LowerKey, _)).

map_lowerValue(Map, Key, LowerValue) :- map_lowerEntry(Map, Key, (_, LowerValue)).

map_lowerEntry(Map, Key, Entry) :- lower_entry(Map, Key, none, Entry), Entry \= none.

