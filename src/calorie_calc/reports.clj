(ns calorie-calc.reports
  "JSON report generation from analyses."
  (:require [calorie-calc.analysis :as analysis]
            [calorie-calc.utils :as utils]))

(defn summary-report
  "Returns general statistical report in JSON."
  [records]
  {:dataset-overview (analysis/dataset-overview records)
   :summary-stats (analysis/summary-stats records)
   :top-caloric-foods (analysis/top-caloric-foods records 10)
   :top-expensive-foods (analysis/top-expensive-foods records 10)
   :top-cheap-foods (analysis/top-cheap-foods records 10)
   :total-calories (utils/sum-by-field records :calories)
   :total-spending (utils/sum-by-field records :price)
   :avg-calories-per-meal (utils/avg-by-field records :calories)})

(defn user-report
  "Returns user report in JSON."
  [records user-id]
  (let [user-records (utils/filter-by-user records user-id)]
    (if (empty? user-records)
      {:error "User not found" :user-id user-id}
      {:user-id user-id
       :total-calories (utils/sum-by-field user-records :calories)
       :avg-daily-calories (get (analysis/avg-daily-calories-by-user records) user-id)
       :macronutrients (get (analysis/macros-by-user records) user-id)
       :total-spending (get (analysis/spending-by-user records) user-id)
       :favorite-foods (get (analysis/favorite-foods-by-user records) user-id)
       :consumption-patterns (get (analysis/consumption-patterns-by-user records) user-id)
       :total-meals (count user-records)
       :user-info {:age (:age (first user-records))
                   :weight (:user_weight (first user-records))}})))

(defn food-report
  "Returns food report in JSON."
  [records food-name]
  (let [food-records (utils/filter-by-food records food-name)]
    (if (empty? food-records)
      {:error "Food not found" :food-name food-name}
      {:food-name food-name
       :avg-calories (get (analysis/avg-calories-by-food records) food-name)
       :avg-price (get (analysis/avg-price-by-food records) food-name)
       :frequency (get (analysis/frequency-by-food records) food-name)
       :cost-benefit-ratio (get (analysis/cost-benefit-ratio records) food-name)
       :avg-macronutrients {:protein (utils/avg-by-field food-records :protein)
                            :carbs (utils/avg-by-field food-records :carbs)
                            :fat (utils/avg-by-field food-records :fat)}
       :total-consumptions (count food-records)})))

(defn comparison-report
  "Returns comparisons in JSON."
  [records dimension]
  (case dimension
    "procedence" (analysis/compare-by-procedence records)
    "meal-type" (analysis/compare-by-meal-type records)
    "users" (analysis/compare-users records)
    {:error "Dimension not supported"
     :supported-dimensions ["procedence" "meal-type" "users"]}))

(defn temporal-report
  "Returns temporal analyses in JSON."
  [records]
  {:consumption-by-weekday (analysis/consumption-by-weekday records)
   :consumption-by-hour (analysis/consumption-by-hour records)
   :consumption-by-period (analysis/consumption-by-period records)
   :temporal-trends (analysis/temporal-trends records)
   :moving-averages-7days (analysis/moving-averages records 7)})

(defn correlation-report
  "Returns correlations in JSON."
  [records]
  {:age-vs-calories (analysis/correlation-age-calories records)
   :weight-vs-calories (analysis/correlation-weight-calories records)
   :price-vs-calories (analysis/correlation-price-calories records)
   :food-weight-vs-calories (analysis/correlation-weight-food-calories records)
   :meal-type-vs-time (analysis/correlation-meal-type-time records)})

