(ns clerk.components.tiles
  (:require [nextjournal.clerk :as clerk]
            [clojure.spec.alpha :as s]))

(s/def ::title string?)
(s/def ::description string?)
(s/def ::preview-image string?)
(s/def ::path string?)

(s/def :clerk.component/grid-tile
  (s/coll-of
   (s/keys :req-un
           [::title
            ::description
            ::preview-image
            ::path])))

(defn grid-tiles
  "A component to generate grid-tiles.
   Courtesy of `clerk-demo` repo"
  [tiles]
  (if (s/valid? :clerk.component/grid-tile tiles)
    (clerk/html
     (into [:div.md:grid.md:gap-8.md:grid-cols-2.pb-8]
           (mapv
            (fn [{:keys [path preview-image title description]}]
              [:a.rounded-lg.shadow-lg.border.border-gray-300.relative.flex.flex-col.hover:border-indigo-600.group.mb-8.md:mb-0
               {:href (clerk/doc-url path) :title path :style {:height 300}}
               [:div.flex-auto.overflow-hidden.rounded-t-md.flex.items-center.px-3.py-4
                [:img {:src preview-image :width "100%" :style {:object-fit "contain"}}]]
               [:div.sans-serif.border-t.border-gray-300.px-4.py-2.group-hover:border-indigo-600
                [:div.font-bold.block.group-hover:text-indigo-600 title]
                [:div.text-xs.text-gray-500.group-hover:text-indigo-600.leading-normal description]]])
            tiles)))
    (throw (ex-info "Invalid Spec" (s/explain-data :clerk.component/grid-tile tiles)))))
