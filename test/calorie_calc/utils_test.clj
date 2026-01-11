(ns calorie-calc.utils-test
  (:require [clojure.test :refer :all]
            [calorie-calc.utils :as utils]))

(deftest test-group-by-field
  (testing "Groups records by field"
    (let [records [{:id 1 :type "breakfast"}
                   {:id 2 :type "lunch"}
                   {:id 3 :type "breakfast"}]]
      (is (= 2 (count (get (utils/group-by-field records :type) "breakfast"))))
      (is (= 1 (count (get (utils/group-by-field records :type) "lunch")))))))

(deftest test-filter-by-field
  (testing "Filters records by field"
    (let [records [{:id 1 :type "breakfast"}
                   {:id 2 :type "lunch"}
                   {:id 3 :type "breakfast"}]]
      (is (= 2 (count (utils/filter-by-field records :type "breakfast"))))
      (is (= 1 (count (utils/filter-by-field records :type "lunch")))))))

(deftest test-calculate-percentages
  (testing "Calculates macronutrient percentages"
    (let [result (utils/calculate-percentages 10 20 10)]
      (is (= 25.0 (:protein result)))
      (is (= 50.0 (:carbs result)))
      (is (= 25.0 (:fat result)))))
  (testing "Returns zeros when total is zero"
    (let [result (utils/calculate-percentages 0 0 0)]
      (is (= 0.0 (:protein result)))
      (is (= 0.0 (:carbs result)))
      (is (= 0.0 (:fat result))))))

(deftest test-parse-date
  (testing "Converts date string to LocalDate"
    (let [date (utils/parse-date "2022-09-25")]
      (is (instance? java.time.LocalDate date))
      (is (= 2022 (.getYear date)))
      (is (= 9 (.getMonthValue date))))))

(deftest test-parse-time
  (testing "Converts time string to LocalTime"
    (let [time (utils/parse-time "11:58")]
      (is (instance? java.time.LocalTime time))
      (is (= 11 (.getHour time)))
      (is (= 58 (.getMinute time))))))

(deftest test-period-of-day
  (testing "Classifies period of the day"
    (is (= "morning" (utils/period-of-day (utils/parse-time "08:00"))))
    (is (= "afternoon" (utils/period-of-day (utils/parse-time "14:00"))))
    (is (= "evening" (utils/period-of-day (utils/parse-time "20:00"))))))

(deftest test-sum-by-field
  (testing "Sums values of a field"
    (let [records [{:calories 100} {:calories 200} {:calories 300}]]
      (is (= 600 (utils/sum-by-field records :calories))))))

(deftest test-avg-by-field
  (testing "Calculates average of values in a field"
    (let [records [{:calories 100} {:calories 200} {:calories 300}]]
      (is (= 200.0 (utils/avg-by-field records :calories)))))
  (testing "Returns zero for empty list"
    (is (= 0.0 (utils/avg-by-field [] :calories)))))

(deftest test-count-by-field
  (testing "Counts records grouped by field"
    (let [records [{:type "breakfast"} {:type "lunch"} {:type "breakfast"}]]
      (is (= 2 (get (utils/count-by-field records :type) "breakfast")))
      (is (= 1 (get (utils/count-by-field records :type) "lunch"))))))

