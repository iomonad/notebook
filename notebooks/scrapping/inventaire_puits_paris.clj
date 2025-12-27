^{:nextjournal.clerk/visibility {:code :hide}}
(ns scrapping.inventaire-puits-paris
  (:require [nextjournal.clerk :as clerk]
            [toolbox.collections :as coll]
            [geospatial.geojson :as geojson]
            [clj-http.client :as http]
            [hickory.core :as hickory]
            [hickory.select :as s]
            [clojure.string :as str]))

;;; # Extraction CFPPHR: Les Puits de Paris

;;; La Commission française pour la protection du patrimoine historique et rural (**C.F.P.P.H.R.**), ainsi que l' Association pour la sauvegarde et l'étude du patrimoine souterrain (A.S.E.P.S.) sont deux associations loi de 1901, très complémentaires qui fonctionnent ensemble.

;;; Nous allons dans ce notebook extraire et consolider le travail de bénédictin de Mr. Cahuzac à recenser les puits historiques dans la Ville de Paris.

;;; L'idee etant de constituer une carte de toutes les adresses.

;;; ## Recuperation des pages via Internet-Archive

;;; Avec le temps, le site perd ses liens et sa structure. Nous allons dans un premier temps recuperer les liens de webarchive d'une snapshot de **2021**

^{::clerk/visibility {:result :hide}}
(def archive-pages
  ["https://web.archive.org/web/20210506135119/http://cfpphr.free.fr/invpuits75a.htm"
   "https://web.archive.org/web/20210506135119/http://cfpphr.free.fr/invpuits75b.htm"])

;;; ### Extraction du HTML

;;; Ici on re-encode le buffer en ISO-8859-1 pour la compat' du set de char

(defonce archived-pages-raw
  (->> archive-pages
       (map (fn [url] (:body (http/get url  {:as :byte-array}))))
       (map #(String. % "ISO-8859-1"))))

;;; Conversion avec un parser Jsoup, puis en format Hiccup

(def archived-pages-parsed
  (->> archived-pages-raw
       (map (comp hickory/as-hickory hickory/parse))))

;;; ## Filtrage de l'essentiel

^{::clerk/auto-expand-results? true}
(def page-POIs
  (->> archived-pages-parsed
       (map (partial s/select (s/child (s/tag :body))))
       (map (comp :content first))
       (map (fn [x] (remove #(= (:type %) :comment) x)))
       (map (fn [html]
              (->> html
                   (drop-while (fn [{:keys [tag]}]
                                 (not= tag :a)))
                   (take-while (fn [{:keys [tag]}]
                                 (not= tag :center)))
                   #_(remove (fn [{:keys [tag]}]
                               (not= tag :br))))))))

;;; Decoupage & triage de la data

^{::clerk/visibility {:result :hide}}
(defn format-well [data]
  (let [[loca description] (str/split data #":")]
    {:well/location (str/trim loca)
     :well/description (str/capitalize (str/trim description))}))

^{::clerk/visibility {:result :hide}}
(defn group-well-data
  [pois]
  (let [splited (->> pois
                     (partition-by (fn [{:keys [tag]}]
                                     (= tag :a))))
        headers (map first (partition-all 2 splited))
        values  (map second (partition-all 2 splited))]
    (->> [(->> headers
               (map (fn [{:keys [content]}]
                      (-> (get-in content [1 :content 0])
                          (str/split #"\(")
                          first
                          str/trim))))
          (->> values
               (map (fn [data]
                      (->> (map (fn [poi]
                                  (-> poi
                                      (str/replace "\n" "")
                                      (str/trim)))
                                data)
                           (keep seq)
                           (map (partial apply str))
                           (remove #(str/starts-with? % "{"))
                           (map format-well)))))]
         (apply zipmap))))

;;; Recuperation des deux pages sous forme de data partiellement enrichies

;;; Utilisation de l'API de Geocoding

^{::clerk/visibility {:result :hide}}
(require '[geospatial.geocoding :as geocode])

(def well-data
  (apply merge (map group-well-data page-POIs)))

;;; Calcul total des puis qui ont ete extraits

(def total-well (reduce + (vals (update-vals well-data count))))

;;; Formatage des donnees, pour le geocodage, generation d'une vue plate

(def well-data-flat
  (->> well-data
       (mapcat (fn [[arrondissement wells]]
                 (map (fn [well]
                        (-> (update well :well/location #(str % " " arrondissement " Paris"))
                            (assoc :well/postal-code arrondissement)))
                      wells)))))

;;; Filtrage sur l'ordre de match elasticsearch, idealement, il faudrait choisir le resultat avec le `:score` le plus haut.

^{::clerk/visibility {:result :hide}}
(defn select-biggest-score
  [elastic-score]
  [(->> (sort-by #(get-in % [:properties :score]) > elastic-score)
        (first))])

;;; Creation d'une fonction utilitaire pour calculer le geocodage de l'adresse.

^{::clerk/visibility {:result :hide}}
(defn geocode-well
  [well]
  (let [geojson-geocode (geocode/geocode (:well/location well) {:service :google})]
    (-> geojson-geocode
        (assoc-in [:features 0 :properties :description] (:well/description well))
        (assoc-in [:features 0 :properties :original-address] (:well/location well))
        (update :features (fn [feat]
                            (filterv #(get-in % [:properties :description])
                                     feat))))))

;;; Calcul de tout le dataset

^{::clerk/auto-expand-results? true}
(def resultats (mapv geocode-well well-data-flat))

;;; On fusionne ici tous les Features collection a un format en une seule FeatureCollection

(def geojson-results (apply coll/deep-merge resultats))

;;; ## Creation du fichier d'export

(comment
  (def clean (geojson/transpose-geojson-coordinates geojson-results (comp vec reverse)))
  (spit "/tmp/puits_v3.geojson" (charred.api/write-json-str clean)))
