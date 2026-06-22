; parser ;

(defn create-parser [constant-type variable-type list-type]
  (letfn [(parser [expr] (cond
                           (number? expr) (constant-type expr)
                           (symbol? expr) (variable-type (name expr))
                           (list? expr) (apply (get list-type (first expr)) (map parser (rest expr)))))] parser))

; hw 10 ;

(defn constant [value] (constantly value))
(defn variable [value] (fn [args] (get args (str (Character/toLowerCase (first value))))))

(defn create-operation [op] (fn [& expr] (fn [args] (apply op (map #(% args) expr)))))

(defn safe-divide ([arg] (/ 1.0 arg)) ([arg & args] (/ (double arg) (double (apply * args)))))
(defn atan12-op ([arg1] (Math/atan arg1)) ([arg2 arg1] (Math/atan2 arg2 arg1)))
(defn period-2 [arg] (if (Double/isFinite (double arg)) (let [r (mod arg 2.0)] (if (>= r 1.0) (- r 2.0) r)) arg))
(defn asin-op [arg] (Math/asin (period-2 arg)))
(defn acos-op [arg] (Math/acos (period-2 arg)))
(defn sumexp-op [& args] (apply + (map #(Math/exp %) args)))
(defn lse-op [& args] (Math/log (apply sumexp-op args)))
(defn cube-op [arg] (* arg arg arg))
(defn square-op [arg] (* arg arg))

(def add (create-operation +))
(def subtract (create-operation -))
(def multiply (create-operation *))
(def divide (create-operation safe-divide))
(def negate (create-operation -))
(def arcSin (create-operation asin-op))
(def arcCos (create-operation acos-op))
(def arcTan12 (create-operation atan12-op))
(def sumExp (create-operation sumexp-op))
(def lse (create-operation lse-op))
(def cube (create-operation cube-op))
(def square (create-operation square-op))

(def operation-list {'+ add '- subtract '* multiply '/ divide 'negate negate 'asin arcSin 'acos arcCos 'atan12 arcTan12 'sumExp sumExp 'lse lse, 'cube cube, 'square square})

(def common-parser
  (create-parser constant variable operation-list))

(defn parseFunction [expr] (common-parser (read-string expr)))

; hw 11 ;

(definterface ObjectExpr (evaluate [args]) (to_string []))

(defn evaluate [expr args] (.evaluate expr args))
(defn toString [expr] (.to_string expr))

(deftype ObjectFunction [op sym expr] ObjectExpr
  (evaluate [this args] (apply op (map #(evaluate % args) expr)))
  (to_string [this] (str "(" sym " " (String/join " " (map toString expr)) ")")))
(deftype ObjectConstant [value] ObjectExpr (evaluate [this args] value) (to_string [this] (str value)))
(deftype ObjectVariable [value] ObjectExpr (evaluate [this args] (get args (str (Character/toLowerCase (first value))))) (to_string [this] value))

(defn create-function [op sym]
  (fn [& expr] (ObjectFunction. op sym expr)))

(def Add (create-function + "+"))
(def Subtract (create-function - "-"))
(def Multiply (create-function * "*"))
(def Divide (create-function safe-divide "/"))
(def Negate (create-function - "negate"))
(def ArcSin (create-function asin-op "asin"))
(def ArcCos (create-function acos-op "acos"))
(def ArcTan12 (create-function atan12-op "atan12"))
(def SumExp (create-function sumexp-op "sumExp"))
(def Lse (create-function lse-op "lse"))
(def Cube (create-function cube-op "cube"))
(def Square (create-function square-op "square"))

(defn Constant [value] (ObjectConstant. value))
(defn Variable [value] (ObjectVariable. value))

(def function-list {'+ Add '- Subtract '* Multiply '/ Divide 'negate Negate 'asin ArcSin 'acos ArcCos 'atan12 ArcTan12 'sumExp SumExp 'lse Lse, 'cube Cube, 'square Square})

(def object-parser
  (create-parser Constant Variable function-list))

(defn parseObject [expr] (object-parser (read-string expr)))

; hw 12 ;

(load-file "parser.clj")

(def postfix-list {"+" Add "-" Subtract "*" Multiply "/" Divide "negate" Negate "asin" ArcSin "acos" ArcCos "atan12" ArcTan12 "sumExp" SumExp "lse" Lse "cube" Cube "square" Square})

(defn postfix-string [expr] (cond (list? expr) (str "(" (String/join " " (concat (map postfix-string (rest expr)) [(str (first expr))])) ")") :else (str expr)))
(defn toStringPostfix [expr] (postfix-string (read-string (toString expr))))

(def postfix-digit (_char #(Character/isDigit %)))
(def postfix-non-zero (+char "123456789"))
(def postfix-letter (+char "xXyYzZ"))
(def postfix-skip-whitespaces (+ignore (+star (_char #(Character/isWhitespace %)))))
(def postfix-number (+seqn 0 (+map read-string (+str (+seq (+opt (+char "-")) (+str (+or (+seq postfix-non-zero (+str (+star postfix-digit))) (+seq (+char "0"))))
                                                           (+opt (+str (+seq (+char ".") (+str (+plus postfix-digit))))))))
                           postfix-skip-whitespaces))

(def postfix-name (+seqn 0 (+str (+plus postfix-letter)) postfix-skip-whitespaces))
(def postfix-token (+seqn 0 (+str (+plus (+char-not " \t\n\r()"))) postfix-skip-whitespaces))

(def postfix-open-bracket (+ignore (+seqn 0 (+char "(") postfix-skip-whitespaces)))
(def postfix-close-bracket (+ignore (+seqn 0 (+char ")") postfix-skip-whitespaces)))

(def postfix-constant (+map Constant postfix-number))
(def postfix-variable (+map Variable postfix-name))
(def postfix-operation (+map #(get postfix-list %) postfix-token))

(declare postfix-expression)
(def postfix-in-brackets (+seqf (fn [args operation] (apply operation args)) postfix-open-bracket (+star (delay postfix-expression)) postfix-operation postfix-close-bracket))
(def postfix-expression (+or postfix-in-brackets postfix-constant postfix-variable))

(def parseObjectPostfix (+parser (+seqn 0 postfix-skip-whitespaces postfix-expression)))