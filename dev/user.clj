(ns user
  (:require [nextjournal.clerk :as clerk]
            [clojure.tools.namespace.repl :refer [refresh]]))

(defn go
  []
  (clerk/serve! {:watch-paths ["notebooks"]
                 :browse? true                 :port 8080})
  (clerk/show! "notebooks/experiments/tailwind.clj"))
