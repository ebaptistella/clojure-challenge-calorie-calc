(ns calorie-calc.data-test
  (:require [clojure.test :refer :all]
            [calorie-calc.data :as data]))

(def sample-record
  {:id "1"
   :user_id "18"
   :age "33"
   :user_weight "91.88"
   :name "Pasta Carbonara"
   :price "10.39"
   :weight "630"
   :calories "383"
   :fat "10.3"
   :carbs "5.95"
   :protein "12.67"
   :time_consumed "11:58"
   :date_consumed "2022-09-25"
   :type "lunch"
   :favorite "false"
   :procedence "purchased"})

(deftest test-normalize-record
  (testing "Converts data types correctly"
    (let [normalized (data/normalize-record sample-record)]
      (is (= 1 (:id normalized)))
      (is (= 18 (:user_id normalized)))
      (is (= 33 (:age normalized)))
      (is (= 91.88 (:user_weight normalized)))
      (is (= 10.39 (:price normalized)))
      (is (= 630 (:weight normalized)))
      (is (= 383 (:calories normalized)))
      (is (= 10.3 (:fat normalized)))
      (is (= 5.95 (:carbs normalized)))
      (is (= 12.67 (:protein normalized)))
      (is (= false (:favorite normalized)))
      (is (instance? java.time.LocalDate (:date_consumed normalized)))
      (is (instance? java.time.LocalTime (:time_consumed normalized))))))

(deftest test-validate-record
  (testing "Validates complete record"
    (is (true? (data/validate-record sample-record))))
  (testing "Rejects incomplete record"
    (is (false? (data/validate-record {:id 1}))))
  (testing "Rejects record without required fields"
    (is (false? (data/validate-record {:id 1 :user_id 18})))))

(deftest test-normalize-all
  (testing "Normalizes multiple records"
    (let [records [sample-record sample-record]
          normalized (data/normalize-all records)]
      (is (= 2 (count normalized)))
      (is (every? #(instance? java.time.LocalDate (:date_consumed %)) normalized))
      (is (every? #(instance? java.time.LocalTime (:time_consumed %)) normalized)))))

(deftest test-validate-data
  (testing "Filters only valid records"
    (let [valid-record sample-record
          invalid-record {:id 1}
          records [valid-record invalid-record]
          validated (data/validate-data records)]
      (is (= 1 (count validated)))
      (is (= valid-record (first validated))))))

