(ns calorie-calc.utils
  "Utility functions for data manipulation, dates, and aggregations."
  (:import [java.time LocalDate LocalTime]))

(defn group-by-field
  "Groups records by a specific field."
  [records field]
  (group-by #(get % field) records))

(defn filter-by-field
  "Filters records by criteria in a field."
  [records field value]
  (filter #(= (get % field) value) records))

(defn filter-by-user
  "Filters records by user_id."
  [records user-id]
  (filter #(= (:user_id %) user-id) records))

(defn filter-by-food
  "Filters records by food name."
  [records food-name]
  (filter #(= (:name %) food-name) records))

(defn calculate-percentages
  "Calculates percentages of macronutrients (protein, carbohydrates, fat).
  Returns a map with the percentages."
  [protein carbs fat]
  (let [total (+ protein carbs fat)]
    (if (zero? total)
      {:protein 0.0 :carbs 0.0 :fat 0.0}
      {:protein (* 100.0 (/ protein total))
       :carbs (* 100.0 (/ carbs total))
       :fat (* 100.0 (/ fat total))})))

(defn parse-date
  "Converts date string (YYYY-MM-DD) to LocalDate."
  [date-str]
  (LocalDate/parse date-str))

(defn parse-time
  "Converts time string (HH:MM) to LocalTime."
  [time-str]
  (LocalTime/parse time-str))

(defn day-of-week
  "Returns the day of the week from a date (1=Monday, 7=Sunday)."
  [date]
  (let [dow (.getValue (.getDayOfWeek date))]
    (if (= dow 7) 0 dow))) ; Adjusts for Sunday = 0

(defn day-of-week-name
  "Returns the name of the day of the week in English."
  [date]
  (let [dow (.getValue (.getDayOfWeek date))
        day-names {1 "Monday" 2 "Tuesday" 3 "Wednesday" 4 "Thursday"
                   5 "Friday" 6 "Saturday" 7 "Sunday"}]
    (get day-names dow "unknown")))

(defn period-of-day
  "Classifies the period of the day based on the hour (morning, afternoon, evening)."
  [time]
  (let [hour (.getHour time)]
    (cond
      (< hour 12) "morning"
      (< hour 18) "afternoon"
      :else "evening")))

(defn sum-by-field
  "Sums values of a specific field."
  [records field]
  (reduce + (map field records)))

(defn avg-by-field
  "Calculates the average of values in a specific field."
  [records field]
  (if (empty? records)
    0.0
    (double (/ (sum-by-field records field) (count records)))))

(defn count-by-field
  "Counts records grouped by a field."
  [records field]
  (->> records
       (group-by #(get % field))
       (map (fn [[k v]] [k (count v)]))
       (into {})))

