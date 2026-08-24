(chapter "Parser macro")

(section "Parser macro definition")

(example "Collect rules"
         (defn- +rules [defs]
           (cond
             (empty? defs) ()
             ; Function: (name args body)
             (seq? (first defs)) (let [[[name args body] & tail] defs]
                                   (cons
                                     {:name name :args args :body body}
                                     (+rules tail)))
             ; Definition: name body
             :else (let [[name body & tail] defs]
                     (cons
                       {:name name :args [] :body body :plain true}
                       (+rules tail)))))
         (+rules '((f [a1 a2] (+ a1 a2))))
         (+rules '(p (f p1 p2)))
         (+rules '((f [a1 a2] (+ a1 a2))
                   p (f p1 p2))))

(example "Converts known values"
         (defn convert [known value]
           (cond
             (seq? value) (map (partial convert known) value)
             (char? value) `(+char ~(str value))
             (contains? known value) `(~value)
             :else value))
         (convert #{'f} '(p (f p1 p2 \c))))

(example "parser macro"
         (defmacro defparser [name & defs]
           (let [rules (+rules defs)
                 known (set (map :name (filter :plain rules)))]
             (letfn [(rule [{name :name, args :args, body :body}] `(~name ~args ~(convert known body)))]
               `(def ~name
                  (letfn
                    ~(mapv rule rules)
                    (+parser (~(:name (last rules)))))))))
         (macroexpand '(defparser a
                                  *a \a))
         (macroexpand '(defparser aa
                                  (*twice [p] (+seq p p))
                                  *a (*twice \a)))
         (defparser aa
                    (*twice [p] (+seq p p))
                    *a (*twice \a))
         (aa "aa")
         (aa "aa~")
         (aa "a"))


(section "Parser macro usage example")

(example "JSON parser"
         (defparser json
                    (*literal [value name] (apply +seqf (constantly value) (map (comp +char str) name)))
                    *null (*literal nil "null")
                    *true (*literal true "true")
                    *false (*literal false "false")
                    *all-chars (mapv char (range 0 128))
                    (*chars [p] (+char (apply str (filter p *all-chars))))
                    *letter (*chars Character/isLetter)
                    *digit (*chars Character/isDigit)
                    *space (*chars Character/isWhitespace)
                    *ws (+ignore (+star *space))
                    *number (+map read-string (+str (+plus *digit)))
                    *identifier (+str (+seqf cons *letter (+star (+or *letter *digit))))
                    *string (+seqn 1 \" (+str (+star (+char-not "\""))) \")
                    (*seq [begin p end]
                          (+seqn 1 begin (+opt (+seqf cons *ws p (+star (+seqn 1 *ws \, *ws p)))) *ws end))
                    *array (+map vec (*seq \[ (delay *value) \]))
                    *member (+seq *identifier *ws (+ignore \:) *ws (delay *value))
                    *object (+map (partial reduce (partial apply assoc) {}) (*seq \{ *member \}))
                    *value (+or *null *true *false *number *string *object *array)
                    *json (+seqn 0 *ws *value *ws))
         (json "[1, {a: \"hello\", b: [1, 2, 3]}, null, true, false]")
         (json "  [1, {a: \"hello\", b: [1, 2, 3]}, null, true, false]   ")
         (json "[1, {a: \"hello\", b: [1, 2, 3]}, null, true, false]~"))
