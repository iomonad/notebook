^{:nextjournal.clerk/visibility {:code :hide}}
(ns games.euler.problem0)

;;; # Problem Zero
;;;  A number is a perfect square, or a square number, if it is the square of a positive integer.

;;;; > Among the first 822 thousand square numbers, what is the sum of all the odd squares?

(reduce + (map #(* % %) (range 1 822000 2)))
