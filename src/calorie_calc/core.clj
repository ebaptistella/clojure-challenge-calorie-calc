(ns calorie-calc.core
  "CLI entry point for the nutritional analysis system."
  (:require [clojure.data.json :as json]
            [clojure.tools.cli :refer [parse-opts]]
            [calorie-calc.data :as data]
            [calorie-calc.reports :as reports])
  (:gen-class))

(def cli-options
  [["-f" "--file FILE" "Path to calories.json file (or URL)"
    :default "https://git.toptal.com/screeners/calories-json/-/raw/main/calories.json"]
   ["-p" "--pretty" "Format JSON with indentation (pretty-print)"]
   ["-h" "--help" "Show this help message"]])

(defn print-usage
  "Prints CLI usage message."
  [options-summary]
  (println "Usage: lein run [command] [arguments] [options]")
  (println)
  (println "Commands:")
  (println "  summary                    - General dataset statistics")
  (println "  user <user-id>             - Analysis of a specific user")
  (println "  food <food-name>           - Analysis of a specific food")
  (println "  compare <dimension>         - Comparisons (procedence, meal-type, users)")
  (println "  temporal                   - Temporal analyses")
  (println "  correlation                - Correlation analyses")
  (println)
  (println "Options:")
  (println options-summary)
  (println)
  (println "Examples:")
  (println "  lein run summary")
  (println "  lein run user 18")
  (println "  lein run food \"Pasta Carbonara\"")
  (println "  lein run compare procedence")
  (println "  lein run --pretty summary"))

(defn convert-to-json-serializable
  "Converts Java types (LocalDate, LocalTime) to strings for JSON serialization."
  [data]
  (cond
    (instance? java.time.LocalDate data)
    (str data)

    (instance? java.time.LocalTime data)
    (str data)

    (map? data)
    (into {} (map (fn [[k v]] [k (convert-to-json-serializable v)]) data))

    (vector? data)
    (mapv convert-to-json-serializable data)

    (seq? data)
    (map convert-to-json-serializable data)

    :else
    data))

(defn output-json
  "Outputs formatted JSON to STDOUT."
  [data pretty?]
  (let [serializable-data (convert-to-json-serializable data)]
    (if pretty?
      (println (json/write-str serializable-data :escape-unicode false :indent true))
      (println (json/write-str serializable-data :escape-unicode false)))))

(defn execute-command
  "Executes the specified CLI command."
  [command args records pretty?]
  (try
    (case command
      "summary" (output-json (reports/summary-report records) pretty?)
      "user" (if (empty? args)
               (println "Error: user-id is required. Use: lein run user <user-id>")
               (let [user-id (Integer/parseInt (first args))]
                 (output-json (reports/user-report records user-id) pretty?)))
      "food" (if (empty? args)
               (println "Error: food name is required. Use: lein run food <food-name>")
               (output-json (reports/food-report records (first args)) pretty?))
      "compare" (if (empty? args)
                  (println "Error: dimension is required. Use: lein run compare <dimension>")
                  (output-json (reports/comparison-report records (first args)) pretty?))
      "temporal" (output-json (reports/temporal-report records) pretty?)
      "correlation" (output-json (reports/correlation-report records) pretty?)
      (do
        (println (str "Error: unknown command: " command))
        (print-usage (:summary (parse-opts [] cli-options)))))
    (catch NumberFormatException _
      (println "Error: user-id must be an integer"))
    (catch Exception e
      (println (str "Error executing command: " (.getMessage e)))
      (.printStackTrace e))))

(defn -main
  "Main entry point for the CLI application."
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options)
      (print-usage summary)

      (seq errors)
      (do
        (println "Errors:")
        (doseq [error errors]
          (println error))
        (print-usage summary))

      :else
      (try
        (let [command (first arguments)
              command-args (rest arguments)
              pretty? (:pretty options)
              file-source (:file options)]
          (if (nil? command)
            (do
              (println "Error: command is required")
              (print-usage summary))
            (let [records (data/load-and-normalize file-source)]
              (execute-command command command-args records pretty?))))
        (catch Exception e
          (println (str "Fatal error: " (.getMessage e)))
          (.printStackTrace e)
          (System/exit 1))))))

