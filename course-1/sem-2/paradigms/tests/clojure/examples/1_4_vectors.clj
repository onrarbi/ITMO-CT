(chapter "Vectors")
(example "Vectors"
         (vector 1 2)
         (vector 1 2 "Hello" 3 4)
         [1 2]
         (def vect [1 2 "Hello" 3 4]))
(example "Vector type"
         (type [1 2])
         (type (vector))
         (type []))
(example "Queries"
         (count vect)
         (nth vect 2)
         (nth vect 20 "none")
         (vect 2)
         (vect 10))
(example "Modifications"
         (conj vect 100 200)
         (peek vect)
         (pop vect)
         (assoc vect 0 100)
         (assoc vect 0 100 2 200))
