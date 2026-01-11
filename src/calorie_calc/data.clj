(ns calorie-calc.data
  "Loading and normalization of data from calories.json file."
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str])
  (:import [java.time LocalDate LocalTime]))

(defn load-data
  "Loads JSON data from local file or remote URL.
  Returns a sequence of records."
  [source]
  (try
    (let [content (if (str/starts-with? source "http")
                    (slurp source)
                    (slurp (io/file source)))]
      (json/read-str content :key-fn keyword))
    (catch Exception e
      (throw (ex-info (str "Error loading data from: " source) {:source source} e)))))

(defn normalize-record
  "Normalizes a record by converting data types.
  Converts numeric strings to numbers, favorite to boolean, etc."
  [record]
  (-> record
      (update :id #(if (string? %) (Integer/parseInt %) %))
      (update :user_id #(if (string? %) (Integer/parseInt %) %))
      (update :age #(if (string? %) (Integer/parseInt %) %))
      (update :user_weight #(if (string? %) (Double/parseDouble %) %))
      (update :price #(if (string? %) (Double/parseDouble %) %))
      (update :weight #(if (string? %) (Integer/parseInt %) %))
      (update :calories #(if (string? %) (Integer/parseInt %) %))
      (update :fat #(if (string? %) (Double/parseDouble %) %))
      (update :carbs #(if (string? %) (Double/parseDouble %) %))
      (update :protein #(if (string? %) (Double/parseDouble %) %))
      (update :favorite #(= "true" (str %)))
      (update :date_consumed #(when % (LocalDate/parse %)))
      (update :time_consumed #(when % (LocalTime/parse %)))))

(defn normalize-all
  "Applies normalization to all records."
  [records]
  (map normalize-record records))

(defn validate-record
  "Validates the structure and types of a record."
  [record]
  (and (map? record)
       (contains? record :id)
       (contains? record :user_id)
       (contains? record :name)
       (contains? record :calories)
       (contains? record :date_consumed)
       (contains? record :type)))

(defn validate-data
  "Validates all records and returns only valid ones."
  [records]
  (filter validate-record records))

(defn load-and-normalize
  "Loads and normalizes data from a source (file or URL).
  Returns normalized and validated data."
  [source]
  (-> source
      load-data
      normalize-all
      validate-data))

