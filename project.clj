(defproject notebook "0.1.0-SNAPSHOT"
  :description "A Clojure / Clerk notebook repo to centralize my experimentations with OpenData, and other sources."
  :url "https://github.com/iomonad/notebook"
  :dependencies [[org.clojure/clojure         "1.12.3"]
                 [io.github.nextjournal/clerk "0.18.1158"]
                 [com.cnuernber/charred       "1.037"]]
  :source-paths ["dev" "notebooks" "src"]
  :aliases {"build-static" ["run" "-m" "build"]}
  :profiles {:dev {:dependencies [[org.clojure/tools.namespace "1.5.0"]]}
             :repl-options {:init-ns user}})
