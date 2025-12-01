^{:nextjournal.clerk/visibility {:code :hide}}
(ns games.euler.problem1)

;;; # Problem 1: Multiples of 3 or 5
;;; If we list all the natural numbers below 10 that are multiples of 3 or 5 , we get 3,5,6 and 9. The sum of these multiples is 23.

;;; > Find the sum of all the multiples of 3 or 5 below 1000.

(def multiples
  (->> (range 1 1000)
       (filter (fn [num]
                 (or (zero? (mod num 3))
                     (zero? (mod num 5)))))
       (reduce +)))
