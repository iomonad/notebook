(ns build
  (:require [nextjournal.clerk :as clerk]))

(defn -main [& _args]
  (clerk/build! {:paths ["notebooks/**"]
                 :package :directory
                 ;;;:index "notebooks/index.clj"
                 }))
