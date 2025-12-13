^{:nextjournal.clerk/visibility {:code :hide}}
(ns experiments.maps
  {:type :experiments
   :title "Clerk & Maps"
   :preview-image "https://images.unsplash.com/photo-1577086664693-894d8405334a?w=900&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTV8fG1hcHxlbnwwfHwwfHx8MA%3D%3D"
   :description "Experiment with Clerk and Javascript Map libraries"}
  (:require [nextjournal.clerk :as clerk]
            [charred.api :as charred]))

;;; # Working with Maps

;;; One good piece of code if feel that Clerk miss is map support. With that feature, we can create some REPL driven Geospatial data analysis.

;;; ## Case study #1: Leaflet

;;; We can include custom CSS as follow in Clerk
^{::clerk/visibility {:result :hide}}
(alter-var-root
 #'nextjournal.clerk.view/include-css+js
 (fn [include-fn]
   (fn [state]
     (concat (include-fn state)
             (list (hiccup.page/include-css "https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"))))))

;;; Let's create a custom implementation of clerk renderer, using d3-require, A minimal, promise-based implementation to require asynchronous module definitions (AMD).
^{::clerk/visibility {:result :hide}}
(def leaflet-geojson-viewer
  {:transform-fn clerk/mark-presented
   :render-fn
   '(fn [[{:keys [initial-view initial-height basemap]
           :as leaflet}
          geojson-data]]
      (when (and leaflet geojson-data)
        [nextjournal.clerk.render/with-d3-require
         {:package ["leaflet@1.9.4/dist/leaflet.min.js"]}
         (fn [leaflet]
           (let [id (str (gensym))
                 m  (atom nil)]
             [:div {:id id
                    :style {:height "600px"}
                    :ref (fn [el]
                           (when el
                             (let [basemap (.tileLayer js/L basemap)]
                               (reset! m (.map js/L id (clj->js {})))
                               (.setView @m (clj->js initial-view) initial-height)
                               (.addTo basemap @m)
                               ;; Populate GeoJSON
                               (.addTo (.geoJson js/L (clj->js geojson-data)) @m))))}]))]))})
;;; We can now define a leaflet spec, passed to the Leaflet builder.

^{::clerk/visibility {:result :hide}}
(def leaflet-spec
  {:initial-view [60.192059 24.945831]
   :initial-height 13
   :basemap "https://tile.openstreetmap.org/{z}/{x}/{y}.png"})

;;; Then generate a convenience function with light API to deal with geospatial data
^{::clerk/visibility {:result :hide}}
(defn leaflet-geojson
  [spec data & {:keys [full-screen]
                :or {full-screen true}}]
  (when-let [input-data (condp = (type data)
                          java.io.File     (charred/read-json (slurp data) :key-fn keyword)
                          java.lang.String (charred/read-json data :key-fn keyword)
                          clojure.lang.PersistentArrayMap data

                          ;; TODO: Implement throw and meanful debugging informations
                          nil)]
    (cond->> (clerk/with-viewer leaflet-geojson-viewer [spec input-data])
      ;; Full screen option
      full-screen (merge {:nextjournal/width :full}))))

;;; Generate our map data

(def data (charred/read-json (slurp "resources/experiments/maps/helsinki_sample.geojson") :key-fn keyword))

;;; And finally our map, with data

(leaflet-geojson leaflet-spec data :full-screen true)

;;; ## Case study #2: Maplibre-GL
