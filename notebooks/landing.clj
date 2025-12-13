^{:nextjournal.clerk/visibility {:code :hide}}
(ns landing
  (:require [nextjournal.clerk :as clerk]
            [clerk.components.tiles :refer [grid-tiles]]
            [clojure.string :as str]))

;;; # Notebooks

^{::clerk/visibility {:code :hide :result :hide}}
(def notebooks
  (->> (all-ns)
       (map (fn [ns]
              (merge (meta ns)
                     {:path (-> (str ns)
                                (str/replace #"\." "/"))})))
       (filter #(get-in % [:type]))))

;;; Here are my notes, used to expose my mess playing around data on the internet.

;;; ## Experimentations

^{::clerk/visibility {:code :hide :result :hide}}
(def experiments
  (filter #(= :experiments (:type %)) notebooks))

^{::clerk/visibility {:code :hide}}
(grid-tiles experiments)

;;; ## Data Analysis

;;; ### Technical Infrastructures

;;; ## Challenges

;;; Here is a list of some code challenge, solved using **Clojure** notebook

;;; ### Advent-of-Code

;;; Advent of Code is an Advent calendar of small programming puzzles for a variety of skill levels that can be solved in any programming language you like.

;;; ### Project Euler

;;; *Project Euler* is a series of challenging mathematical/computer programming problems that will require **more** than just mathematical insights to solve.
