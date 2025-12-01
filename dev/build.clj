(ns build
  (:require [nextjournal.clerk :as clerk]))

(defn -main [& _args]
  (clerk/build! {:paths ["notebooks/*"]
                 ;;;:index "notebooks/index.clj"
                 }))
