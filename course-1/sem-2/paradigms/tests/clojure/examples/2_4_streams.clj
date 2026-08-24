(chapter "Streams")

(section "Definitions")

(example "Setup namespace"
         (in-ns 'info.kgeorgiy.streams)
         (clojure.core/refer 'clojure.core :only '[defn refer alias force delay = inc dec pos? let cond])
         (alias 'c 'clojure.core)
         (refer 'user :only '[example section]))

(example "Base definitions"
         (defn cons [head tail] [head tail])
         (defn first [[head _]] head)
         (defn rest [[_ tail]] (force tail))
         (def empty nil)
         (defn empty? [stream] (= empty stream)))

(example "Basic functions"
         (defn count [stream]
           (if (empty? stream)
             0
             (inc (count (rest stream)))))
         (count empty)
         (count (cons 1 (cons 2 empty)))
         (defn to-list [stream]
           (if (empty? stream)
             ()
             (c/cons (first stream) (to-list (rest stream)))))
         (to-list (cons 1 (cons 2 empty))))

(example "Map and Filter"
         (defn map [f stream]
           (if (empty? stream)
             empty
             (cons (f (first stream)) (delay (map f (rest stream))))))
         (to-list (map inc (cons 1 (cons 2 empty))))
         (defn filter [p? stream]
           (if (empty? stream)
             empty
             (let [head (first stream)
                   tail (delay (filter p? (rest stream)))]
               (if (p? head)
                 (cons head tail)
                 (force tail)))))
         (to-list (filter pos? (cons 1 (cons 2 empty))))
         (to-list (filter pos? (cons 1 (cons -2 empty))))
         (to-list (filter pos? (cons -1 (cons 2 empty))))
         (to-list (filter pos? (cons -1 (cons -2 empty)))))

(example "Take"
         (defn take [n stream]
           (cond
             (empty? stream) empty
             (pos? n) (cons (first stream) (delay (take (dec n) (rest stream))))
             :else empty))
         (to-list (take 0 (cons 1 (cons 2 empty))))
         (to-list (take 1 (cons 1 (cons 2 empty))))
         (to-list (take 2 (cons 1 (cons 2 empty))))
         (to-list (take 3 (cons 1 (cons 2 empty)))))

(example "Take While"
         (defn take-while [p? stream]
           (cond
             (empty? stream) empty
             (p? (first stream)) (cons (first stream) (delay (take-while p? (rest stream))))
             :else empty))
         (to-list (take-while pos? (cons 1 (cons 2 empty))))
         (to-list (take-while pos? (cons 1 (cons -2 empty))))
         (to-list (take-while pos? (cons -1 (cons 2 empty))))
         (to-list (take-while pos? (cons -1 (cons -2 empty)))))

(example "Some and Every"
         (defn some [p? stream]
           (cond
             (empty? stream) false
             (p? (first stream)) true
             :else (some p? (rest stream))))
         (some pos? (cons 1 (cons 2 empty)))
         (some pos? (cons 1 (cons -2 empty)))
         (some pos? (cons -1 (cons 2 empty)))
         (some pos? (cons -1 (cons -2 empty)))
         (defn every [p? stream]
           (cond
             (empty? stream) true
             (p? (first stream)) (every p? (rest stream))
             :else false))
         (every pos? (cons 1 (cons 2 empty)))
         (every pos? (cons 1 (cons -2 empty)))
         (every pos? (cons -1 (cons 2 empty)))
         (every pos? (cons -1 (cons -2 empty))))

(section "Usage")

(example "Finite streams"
         (empty? empty)
         (empty? (cons 1 empty))
         (def s123 (cons 1 (cons 2 (cons 3 empty))))
         s123
         (count empty)
         (count s123)
         (to-list s123)
         (map #(c/+ % %) s123)
         (to-list (map #(c/+ % %) s123))
         (to-list (filter c/odd? s123))
         (count (take 2 s123))
         (to-list (take 2 s123))
         (to-list (take-while (c/partial c/>= 2) s123))
         (some (c/partial = 2) s123)
         (every (c/partial = 4) s123))

(example "Infinite streams"
         (defn sample [stream] (to-list (take 30 stream)))
         (def ones (cons 1 (delay ones)))
         (sample ones)
         (defn integers [i] (cons i (delay (integers (inc i)))))
         (sample (integers 0))
         (sample (take-while #(c/<= % 10) (integers 0)))
         (some #(c/>= % 10) (integers 0))
         (some #(c/<= % 10) (integers 0))
         (every #(c/>= % 10) (integers 0))
         (every #(c/<= % 10) (integers 0)))

(example "Changing namespace"
         (in-ns 'user)
         (alias 'ks 'info.kgeorgiy.streams))

(example "Prime numbers"
         (def primes
           (letfn [(prime? [n]
                     (not (ks/some #(zero? (mod n %)) (ks/take-while #(>= n (* % %)) primes))))]
             (ks/cons 2 (delay (ks/filter prime? (ks/integers 3))))))
         (ks/sample primes)
         (ks/sample (ks/map (partial * 10) primes)))

(section "Lazy sequences")

(example "Infinite sequences"
         (defn sample [seq] (apply list (take 30 seq)))
         (def ones (cons 1 (lazy-seq ones)))
         (sample ones)
         (defn integers [i] (cons i (lazy-seq (integers (inc i)))))
         (sample (integers 0))
         (sample (take-while #(<= % 10) (integers 0)))
         (some #(>= % 10) (integers 0))
         (some #(<= % 10) (integers 0))
         (every? #(>= % 10) (integers 0))
         (every? #(<= % 10) (integers 0)))

(example "Lazy prime numbers"
         (def primes
           (letfn [(prime? [n]
                     (not (some #(zero? (mod n %)) (take-while #(>= n (* % %)) primes))))]
             (cons 2 (filter prime? (integers 3)))))
         (sample primes))

(example "Lazy input"
         (defn lazy-input []
           (let [line (read-line)]
             (if (not (or (empty? line) (= "." line)))
               (cons line (lazy-seq (lazy-input))))))
             (with-in-file "data/sum.in" #(sample (lazy-input))))

(example "Running sum with lazy IO"
         (defn with-lazy-io [init f]
           (letfn [(f' [state input]
             (let [[state' output] (f state input)]
               (println output)
               state'))]
           (fn [] (reduce f' init (lazy-input)))))
         (def running-sum
           (with-lazy-io
             0
             (fn [sum line]
               (let [sum' (+ sum (read-string line))]
                [sum' (str sum')]))))
         (with-in-file "data/sum.in" running-sum))
