(ns build
  (:require [nextjournal.clerk :as clerk]))

(defn -main [& _args]
  (clerk/build! {:paths ["notebooks/**"]
                 ;;:compile-css true
                 :index "notebooks/landing.clj"
                 :package :directory}))
