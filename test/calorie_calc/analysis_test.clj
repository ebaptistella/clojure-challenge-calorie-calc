(ns calorie-calc.analysis-test
  (:require [clojure.test :refer :all]
            [calorie-calc.analysis :as analysis])
  (:import [java.time LocalDate LocalTime]))

(def sample-records
  [{:id 1 :user_id 1 :age 30 :user_weight 70.0 :name "Food A" :price 10.0 :weight 100 :calories 200 :fat 5.0 :carbs 20.0 :protein 15.0
    :time_consumed (LocalTime/parse "12:00") :date_consumed (LocalDate/parse "2022-09-25") :type "lunch" :favorite false :procedence "purchased"}
   {:id 2 :user_id 1 :age 30 :user_weight 70.0 :name "Food B" :price 15.0 :weight 150 :calories 300 :fat 10.0 :carbs 30.0 :protein 20.0
    :time_consumed (LocalTime/parse "19:00") :date_consumed (LocalDate/parse "2022-09-26") :type "dinner" :favorite true :procedence "homemade"}
   {:id 3 :user_id 2 :age 40 :user_weight 80.0 :name "Food A" :price 10.0 :weight 100 :calories 200 :fat 5.0 :carbs 20.0 :protein 15.0
    :time_consumed (LocalTime/parse "08:00") :date_consumed (LocalDate/parse "2022-09-25") :type "breakfast" :favorite false :procedence "purchased"}])

(deftest test-total-calories-by-user
  (testing "Calculates total consumption per user"
    (let [result (analysis/total-calories-by-user sample-records)]
      (is (= 500 (get result 1)))
      (is (= 200 (get result 2))))))

(deftest test-avg-daily-calories-by-user
  (testing "Calculates daily average per user"
    (let [result (analysis/avg-daily-calories-by-user sample-records)]
      (is (= 250.0 (get result 1)))
      (is (= 200.0 (get result 2))))))

(deftest test-spending-by-user
  (testing "Calculates total spending per user"
    (let [result (analysis/spending-by-user sample-records)]
      (is (= 25.0 (get result 1)))
      (is (= 10.0 (get result 2))))))

(deftest test-avg-calories-by-food
  (testing "Calculates average calories per food"
    (let [result (analysis/avg-calories-by-food sample-records)]
      (is (= 200.0 (get result "Food A")))
      (is (= 300.0 (get result "Food B"))))))

(deftest test-frequency-by-food
  (testing "Calculates consumption frequency per food"
    (let [result (analysis/frequency-by-food sample-records)]
      (is (= 2 (get result "Food A")))
      (is (= 1 (get result "Food B"))))))

(deftest test-top-caloric-foods
  (testing "Returns most caloric foods"
    (let [result (analysis/top-caloric-foods sample-records 2)]
      (is (contains? result "Food B"))
      (is (contains? result "Food A")))))

(deftest test-compare-by-procedence
  (testing "Compares characteristics by procedence"
    (let [result (analysis/compare-by-procedence sample-records)]
      (is (contains? result :homemade))
      (is (contains? result :purchased))
      (is (= 2 (get-in result [:purchased :total-count])))
      (is (= 1 (get-in result [:homemade :total-count]))))))

(deftest test-compare-by-meal-type
  (testing "Compares characteristics by meal type"
    (let [result (analysis/compare-by-meal-type sample-records)]
      (is (contains? result "breakfast"))
      (is (contains? result "lunch"))
      (is (contains? result "dinner")))))

(deftest test-dataset-overview
  (testing "Returns dataset overview"
    (let [result (analysis/dataset-overview sample-records)]
      (is (= 3 (:total-records result)))
      (is (= 2 (:unique-users result)))
      (is (= 2 (:unique-foods result))))))

