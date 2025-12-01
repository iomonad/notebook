^{:nextjournal.clerk/visibility {:code :hide}}
(ns games.euler.index
  (:require [clojure.string :as str]
            [nextjournal.clerk :as clerk]))

;;; # About Project Euler
;;; ## What is Project Euler?

;;; Project Euler is a series of challenging mathematical/computer programming problems that will require more than just mathematical insights to solve. Although mathematics will help you arrive at elegant and efficient methods, the use of a computer and programming skills will be required to solve most problems.

;;; The motivation for starting Project Euler, and its continuation, is to provide a platform for the inquiring mind to delve into unfamiliar areas and learn new concepts in a fun and recreational context.

;;; ## Assignments

(def assignments
  (->> (all-ns)
       (filter (fn [ns]
                 (str/starts-with? (name (ns-name ns)) "games.euler.problem")))
       (map meta)))

^{:nextjournal.clerk/visibility {:code :hide}}
;;; courtesy of `clerk-demo` repo
(clerk/html
 (into
  [:div.md:grid.md:gap-8.md:grid-cols-2.pb-8]
  (map
   (fn [{:keys [path preview title description]}]
     [:a.rounded-lg.shadow-lg.border.border-gray-300.relative.flex.flex-col.hover:border-indigo-600.group.mb-8.md:mb-0
      {:href (clerk/doc-url path) :title path :style {:height 300}}
      [:div.flex-auto.overflow-hidden.rounded-t-md.flex.items-center.px-3.py-4
       [:img {:src preview :width "100%" :style {:object-fit "contain"}}]]
      [:div.sans-serif.border-t.border-gray-300.px-4.py-2.group-hover:border-indigo-600
       [:div.font-bold.block.group-hover:text-indigo-600 title]
       [:div.text-xs.text-gray-500.group-hover:text-indigo-600.leading-normal description]]])
   assignments)))
