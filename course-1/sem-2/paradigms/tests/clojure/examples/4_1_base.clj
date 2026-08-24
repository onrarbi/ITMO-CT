(chapter "Basic definitions")

(section "Result and debug functions")

(example "Result"
         (defn -return [value tail] {:value value :tail tail})
         (def -valid? boolean)
         (def -value :value)
         (def -tail :tail))

(example "Debug"
         (defn -show [result]
           (if (-valid? result)
             (str
               "-> " (pr-str (-value result))
               " | " (pr-str (apply str (-tail result))))
             "!"))
         (defn tabulate [parser inputs]
           (run! (fn [input] (printf "    %-10s %s\n" (pr-str input) (-show (parser input))))
                 inputs)))


(section "Basic parsers")

(example "_empty: empty parser"
         ; (defn _empty [value]
         ;   (fn [input] (-return value input)))
         (def _empty (partial partial -return))
         (tabulate (_empty 1) ["" "~"]))

(example "_char: single character matching predicate"
         (defn _char [p]
           (fn [[c & cs]]
             (if (and (char? c) (p c))
               (-return c cs))))
         (tabulate (_char #{\a \b \c}) ["ax" "by" "" "a" "x" "xa"])
         (tabulate (_char (comp not #{\a \b \c})) ["ax" "by" "" "a" "x" "xa"]))


(section "Basic combinators")

(example "_either: either of two parsers"
         (defn _either [a b]
           (fn [input]
             (let [ar ((force a) input)]
               (if (-valid? ar)
                 ar
                 ((force b) input)))))
         (tabulate (_either (_char #{\a}) (_char #{\b})) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa" "ay" "xx"]))

(example "_map: apply function to the parser result"
         (defn _map [f a]
           (fn [input]
             (let [ar ((force a) input)]
               (if (-valid? ar)
                 (-return (f (-value ar)) (-tail ar))))))
         (tabulate (_map Character/toUpperCase (_char #{\a \b \c})) ["a" "a~" "b" "b~" "" "x" "x~"]))

(example "_combine: combine sequence of two parsers using function f"
         (defn _combine [f a b]
           (fn [input]
             (let [ar ((force a) input)]
               (if (-valid? ar)
                 (let [br ((force b) (-tail ar))]
                   (if (-valid? br)
                       (-return (f (-value ar) (-value br)) (-tail br))))))))
         (tabulate (_combine str (_char #{\a \b}) (_char #{\x})) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa" "ay" "xx"]))


(section "Full parser")

(example "_parser: string parser"
         (defn _parser [parser]
           (let [pp (_combine (fn [v _] v) parser (_char #{\u0000}))]
             (comp -value pp #(str % \u0000))))
         (mapv (_parser (_combine str (_char #{\a \b}) (_char #{\x}))) ["ax" "ax~" "bx" "bx~" "" "a" "x" "xa" "ay" "xx"]))
