^{:nextjournal.clerk/visibility {:code :hide}}
(ns scrapping.inventaire-puits-paris
  (:require [nextjournal.clerk :as clerk]
            [clj-http.client :as http]
            [hickory.core :as hickory]
            [hickory.select :as s]
            [clojure.string :as str]))

;;; # Extraction CFPPHR: Les Puits de Paris

;;; La Commission française pour la protection du patrimoine historique et rural (**C.F.P.P.H.R.**), ainsi que l' Association pour la sauvegarde et l'étude du patrimoine souterrain (A.S.E.P.S.) sont deux associations loi de 1901, très complémentaires qui fonctionnent ensemble.

;;; Nous allons dans ce notebook extraire et consolider le travail de bénédictin Mr. Cahuzac à recenser les puits historiques dans la Ville de Paris.

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

;;; Selection de l'essentiel

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
(defn group-well-data
  [pois]
  (let [splited (->> pois
                     (partition-by (fn [{:keys [tag]}]
                                     (= tag :a))))
        headers (map first (partition-all 2 splited))
        values  (map second (partition-all 2 splited))]
    {:headers
     (->> headers
          (map (fn [{:keys [content]}]
                 {:paris/arrondissement (-> (get-in content [1 :content 0])
                                            (str/split #"\(")
                                            first
                                            str/trim)})))
     :values
     (->> values
          (map (fn [data]
                 (->> (map (fn [poi]
                             (-> poi
                                 (str/replace "\n" "")
                                 (str/trim)))
                           data)
                      (keep seq)
                      (map (partial apply str))))))}))

;;; Recuperation des deux pages

(group-well-data (first page-POIs))
