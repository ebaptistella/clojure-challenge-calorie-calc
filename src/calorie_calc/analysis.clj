(ns calorie-calc.analysis
  "Statistical analyses of nutritional data."
  (:require [calorie-calc.utils :as utils]
            [fastmath.stats :as stats])
  (:import [java.time.temporal WeekFields]
           [java.util Locale]))

(defn total-calories-by-user
  "Calculates total caloric consumption per user."
  [records]
  (->> (utils/group-by-field records :user_id)
       (map (fn [[user-id user-records]]
              [user-id (utils/sum-by-field user-records :calories)]))
       (into {})))

(defn avg-daily-calories-by-user
  "Calculates average daily calories per user."
  [records]
  (->> (utils/group-by-field records :user_id)
       (map (fn [[user-id user-records]]
              (let [total-calories (utils/sum-by-field user-records :calories)
                    unique-dates (count (distinct (map :date_consumed user-records)))]
                [user-id (if (zero? unique-dates)
                           0.0
                           (double (/ total-calories unique-dates)))])))
       (into {})))

(defn macros-by-user
  "Calculates macronutrient distribution per user."
  [records]
  (->> (utils/group-by-field records :user_id)
       (map (fn [[user-id user-records]]
              (let [total-protein (utils/sum-by-field user-records :protein)
                    total-carbs (utils/sum-by-field user-records :carbs)
                    total-fat (utils/sum-by-field user-records :fat)]
                [user-id (utils/calculate-percentages total-protein total-carbs total-fat)])))
       (into {})))

(defn spending-by-user
  "Calculates total spending per user."
  [records]
  (->> (utils/group-by-field records :user_id)
       (map (fn [[user-id user-records]]
              [user-id (utils/sum-by-field user-records :price)]))
       (into {})))

(defn favorite-foods-by-user
  "Identifies favorite foods per user."
  [records]
  (let [favorite-records (filter :favorite records)]
    (->> (utils/group-by-field favorite-records :user_id)
         (map (fn [[user-id user-records]]
                [user-id (map :name user-records)]))
         (into {}))))

(defn consumption-patterns-by-user
  "Analyzes consumption time patterns per user."
  [records]
  (->> (utils/group-by-field records :user_id)
       (map (fn [[user-id user-records]]
              (let [periods (map utils/period-of-day (map :time_consumed user-records))]
                [user-id (utils/count-by-field (map (fn [p] {:period p}) periods) :period)])))
       (into {})))

(defn avg-calories-by-food
  "Calculates average calories per food."
  [records]
  (->> (utils/group-by-field records :name)
       (map (fn [[food-name food-records]]
              [food-name (utils/avg-by-field food-records :calories)]))
       (into {})))

(defn avg-price-by-food
  "Calculates average price per food."
  [records]
  (->> (utils/group-by-field records :name)
       (map (fn [[food-name food-records]]
              [food-name (utils/avg-by-field food-records :price)]))
       (into {})))

(defn frequency-by-food
  "Calculates consumption frequency per food."
  [records]
  (->> (utils/group-by-field records :name)
       (map (fn [[food-name food-records]]
              [food-name (count food-records)]))
       (into {})))

(defn top-caloric-foods
  "Returns the most caloric foods."
  [records n]
  (->> records
       avg-calories-by-food
       (sort-by second >)
       (take n)
       (into {})))

(defn top-expensive-foods
  "Returns the most expensive foods."
  [records n]
  (->> records
       avg-price-by-food
       (sort-by second >)
       (take n)
       (into {})))

(defn top-cheap-foods
  "Returns the cheapest foods."
  [records n]
  (->> records
       avg-price-by-food
       (sort-by second <)
       (take n)
       (into {})))

(defn cost-benefit-ratio
  "Calculates cost-benefit ratio (calories per monetary unit) per food."
  [records]
  (->> (utils/group-by-field records :name)
       (map (fn [[food-name food-records]]
              (let [avg-calories (utils/avg-by-field food-records :calories)
                    avg-price (utils/avg-by-field food-records :price)]
                [food-name (if (zero? avg-price)
                             0.0
                             (/ avg-calories avg-price))])))
       (into {})))

(defn compare-by-procedence
  "Compares characteristics between homemade and purchased foods."
  [records]
  (let [grouped (utils/group-by-field records :procedence)
        homemade (get grouped "homemade" [])
        purchased (get grouped "purchased" [])]
    {:homemade {:avg-calories (utils/avg-by-field homemade :calories)
                :avg-price (utils/avg-by-field homemade :price)
                :total-count (count homemade)
                :avg-protein (utils/avg-by-field homemade :protein)
                :avg-carbs (utils/avg-by-field homemade :carbs)
                :avg-fat (utils/avg-by-field homemade :fat)}
     :purchased {:avg-calories (utils/avg-by-field purchased :calories)
                 :avg-price (utils/avg-by-field purchased :price)
                 :total-count (count purchased)
                 :avg-protein (utils/avg-by-field purchased :protein)
                 :avg-carbs (utils/avg-by-field purchased :carbs)
                 :avg-fat (utils/avg-by-field purchased :fat)}}))

(defn compare-by-meal-type
  "Compares characteristics by meal type."
  [records]
  (->> (utils/group-by-field records :type)
       (map (fn [[meal-type meal-records]]
              [meal-type {:avg-calories (utils/avg-by-field meal-records :calories)
                          :total-count (count meal-records)
                          :avg-protein (utils/avg-by-field meal-records :protein)
                          :avg-carbs (utils/avg-by-field meal-records :carbs)
                          :avg-fat (utils/avg-by-field meal-records :fat)}]))
       (into {})))

(defn compare-users
  "Compares users (caloric consumption ranking and spending patterns)."
  [records]
  (let [calories-by-user (total-calories-by-user records)
        spending-by-user (spending-by-user records)]
    {:calories-ranking (->> calories-by-user
                            (sort-by second >)
                            (map (fn [[user-id calories]]
                                   {:user-id user-id :total-calories calories})))
     :spending-ranking (->> spending-by-user
                            (sort-by second >)
                            (map (fn [[user-id spending]]
                                   {:user-id user-id :total-spending spending})))}))

(defn summary-stats
  "Calculates general descriptive statistics."
  [records]
  (let [calories (map :calories records)
        prices (map :price records)
        weights (map :weight records)]
    {:calories {:mean (stats/mean calories)
                :median (stats/median calories)
                :std-dev (stats/stddev calories)
                :min (apply min calories)
                :max (apply max calories)}
     :prices {:mean (stats/mean prices)
              :median (stats/median prices)
              :std-dev (stats/stddev prices)
              :min (apply min prices)
              :max (apply max prices)}
     :weights {:mean (stats/mean weights)
               :median (stats/median weights)
               :std-dev (stats/stddev weights)
               :min (apply min weights)
               :max (apply max weights)}}))

(defn dataset-overview
  "Returns dataset overview."
  [records]
  (let [dates (filter some? (map :date_consumed records))
        sorted-dates (sort dates)]
    {:total-records (count records)
     :unique-users (count (distinct (map :user_id records)))
     :unique-foods (count (distinct (map :name records)))
     :date-range {:start (first sorted-dates)
                  :end (last sorted-dates)}
     :meal-types (utils/count-by-field records :type)
     :procedences (utils/count-by-field records :procedence)}))

(defn consumption-by-weekday
  "Calculates consumption by day of the week."
  [records]
  (let [records-with-weekday (map (fn [r] (assoc r :weekday (utils/day-of-week-name (:date_consumed r)))) records)]
    (->> (utils/group-by-field records-with-weekday :weekday)
         (map (fn [[weekday weekday-records]]
                [weekday {:total-calories (utils/sum-by-field weekday-records :calories)
                          :avg-calories (utils/avg-by-field weekday-records :calories)
                          :count (count weekday-records)}]))
         (into {}))))

(defn consumption-by-hour
  "Calculates consumption by hour of the day."
  [records]
  (let [records-with-hour (map (fn [r] (assoc r :hour (.getHour (:time_consumed r)))) records)]
    (->> (utils/group-by-field records-with-hour :hour)
         (map (fn [[hour hour-records]]
                [hour {:total-calories (utils/sum-by-field hour-records :calories)
                       :count (count hour-records)}]))
         (into {}))))

(defn consumption-by-period
  "Calculates consumption by period of the day (morning, afternoon, evening)."
  [records]
  (let [records-with-period (map (fn [r] (assoc r :period (utils/period-of-day (:time_consumed r)))) records)]
    (->> (utils/group-by-field records-with-period :period)
         (map (fn [[period period-records]]
                [period {:total-calories (utils/sum-by-field period-records :calories)
                         :avg-calories (utils/avg-by-field period-records :calories)
                         :count (count period-records)}]))
         (into {}))))

(defn temporal-trends
  "Calculates trends over time (grouped by week)."
  [records]
  (let [records-with-week (map (fn [r]
                                 (let [date (:date_consumed r)
                                       week-fields (WeekFields/of (Locale/getDefault))
                                       week (.get date (.weekOfYear week-fields))]
                                   (assoc r :week week)))
                               records)]
    (->> (utils/group-by-field records-with-week :week)
         (map (fn [[week week-records]]
                [week {:total-calories (utils/sum-by-field week-records :calories)
                       :avg-calories (utils/avg-by-field week-records :calories)
                       :count (count week-records)}]))
         (sort-by first)
         (into {}))))

(defn moving-averages
  "Calculates moving averages to identify trends."
  [records window-size]
  (let [weekly-data (temporal-trends records)
        weeks (sort (keys weekly-data))
        weekly-calories (map (fn [w] (get-in weekly-data [w :total-calories])) weeks)]
    (when (>= (count weekly-calories) window-size)
      (->> (range (- (count weekly-calories) window-size -1))
           (map (fn [i]
                  (let [window (take window-size (drop i weekly-calories))]
                    [(nth weeks (+ i window-size -1))
                     {:moving-avg (/ (reduce + window) window-size)}])))
           (into {})))))

(defn correlation-age-calories
  "Calculates correlation between age and caloric consumption."
  [records]
  (let [user-data (->> (utils/group-by-field records :user_id)
                       (map (fn [[_user-id user-records]]
                              (let [first-record (first user-records)]
                                {:age (:age first-record)
                                 :avg-calories (utils/avg-by-field user-records :calories)})))
                       (remove #(nil? (:age %))))
        ages (map :age user-data)
        calories (map :avg-calories user-data)]
    (if (and (> (count ages) 1) (not (apply = ages)))
      (stats/correlation ages calories)
      0.0)))

(defn correlation-weight-calories
  "Calculates correlation between user weight and caloric consumption."
  [records]
  (let [user-data (->> (utils/group-by-field records :user_id)
                       (map (fn [[_user-id user-records]]
                              (let [first-record (first user-records)]
                                {:weight (:user_weight first-record)
                                 :avg-calories (utils/avg-by-field user-records :calories)})))
                       (remove #(nil? (:weight %))))
        weights (map :weight user-data)
        calories (map :avg-calories user-data)]
    (if (and (> (count weights) 1) (not (apply = weights)))
      (stats/correlation weights calories)
      0.0)))

(defn correlation-price-calories
  "Calculates correlation between price and calories."
  [records]
  (let [prices (map :price records)
        calories (map :calories records)]
    (if (and (> (count prices) 1) (not (apply = prices)))
      (stats/correlation prices calories)
      0.0)))

(defn correlation-weight-food-calories
  "Calculates correlation between food weight and calories."
  [records]
  (let [weights (map :weight records)
        calories (map :calories records)]
    (if (and (> (count weights) 1) (not (apply = weights)))
      (stats/correlation weights calories)
      0.0)))

(defn correlation-meal-type-time
  "Calculates correlation between meal type and time (simplified)."
  [records]
  (let [meal-type-nums {"breakfast" 1 "lunch" 2 "dinner" 3 "snack" 4}
        hours (map (fn [r] (.getHour (:time_consumed r))) records)
        meal-types (map (fn [r] (get meal-type-nums (:type r) 0)) records)]
    (if (and (> (count hours) 1) (not (apply = hours)))
      (stats/correlation hours meal-types)
      0.0)))

