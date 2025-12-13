^{:nextjournal.clerk/visibility {:code :hide}}
(ns experiments.tailwind
  {:type :experiments
   :title "Tailwind with Clerk"
   :preview-image "https://tailwindcss.com/_next/static/media/tailwindcss-mark.d52e9897.svg"
   :description "Experimental components in Clerk using Tailwind"}
  (:require [nextjournal.clerk :as clerk]))

;;; # Tailwind with Clerk

;;; Some experimentations with custom components made using TailwindCSS library (included in Clerk).

;;; This aims to keep traces of developpement of this notebook.

;;; ## Full grid tiles

;;; Generate a full grid tiles of cat

^{::clerk/visibility {:code :hide}
  ::clerk/no-cache   true}
(clerk/row
 {::clerk/width :full}
 (clerk/html
  (into [:div.md:grid.md:gap-8.md:grid-cols-10.pb-8.px-60]
        (mapv
         (fn [{:keys [path preview-image title description]}]
           [:a.rounded-lg.shadow-lg.border.border-gray-300.relative.flex.flex-col.hover:border-indigo-600.group.mb-8.md:mb-0
            {:href (clerk/doc-url path) :title path :style {:height 300}}
            [:div.flex-auto.overflow-hidden.rounded-t-md.flex.item-center.px-3.py-4
             [:img {:src preview-image :width "100%" :style {:object-fit "contain"}}]]
            [:div.sans-serif.border-t.border-gray-300.px-4.py-2.group-hover:border-indigo-600
             [:div.font-bold.block.group-hover:text-indigo-600 title]
             [:div.text-xs.text-gray-500.group-hover:text-indigo-600.leading-normal description]]])
         (repeatedly 20 (constantly {:title "Foo Bar Baz"
                                     :preview-image "https://cataas.com/cat"
                                     :description "Quux Baz Clojure"}))))))
