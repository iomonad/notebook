(ns user
  (:require [nextjournal.clerk :as clerk]))

(defn go
  []
  (clerk/serve! {:watch-paths ["notebooks"]
                 :browse? true
                 :port 8080})
  (clerk/show! "notebooks/index.clj"))
