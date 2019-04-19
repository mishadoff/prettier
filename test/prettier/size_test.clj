(ns prettier.size-test
  (:require [clojure.test :refer :all]
            [prettier.size :as size]))

(deftest bytes->readable--defaults-test
  (is (= "0 B" (size/bytes->readable 0)))
  (is (= "1000 B" (size/bytes->readable 1000)))
  (is (= "1.0 KB" (size/bytes->readable 1024)))
  (is (= "1.2 KB" (size/bytes->readable 1224)))
  (is (= "1.0 MB" (size/bytes->readable (* 1024 1024))))
  (is (= "2.5 GB" (size/bytes->readable 2732746123)))
  (is (= "7.1 TB" (size/bytes->readable 7823645782369)))
  (is (= "1.5 PB" (size/bytes->readable 1726387216472146)))
  (is (= "8.9 PB" (size/bytes->readable 10000000000000000)))
  (is (= "8.7 EB" (size/bytes->readable 10000000000000000000)))
  (is (= "8.5 ZB" (size/bytes->readable 10000000000000000000000)))
  (is (= "8.3 YB" (size/bytes->readable 10000000000000000000000000)))
  (is (= "Infinity" (size/bytes->readable 10000000000000000000000000000))))

(deftest bytes->readable--errors-test
  (is (thrown? AssertionError (size/bytes->readable -1)))
  (is (thrown? AssertionError (size/bytes->readable -1024)))
  (is (thrown? AssertionError (size/bytes->readable "ef")))
  (is (thrown? AssertionError (size/bytes->readable {:a [1 2]}))))

(deftest bytes->readable--customizations-test
  (binding [size/*size-abbreviations* ["bytes" "kilobytes" "megabytes" "gigabytes"]
            size/*size-decimal-format* "%.0f"
            size/*size-over-limit* "...too much..."
            size/*size-gap* ""]
    (is (= "5bytes" (size/bytes->readable 5)))
    (is (= "22kilobytes" (size/bytes->readable 23000)))
    (is (= "3megabytes" (size/bytes->readable 2723446)))
    (is (= "3gigabytes" (size/bytes->readable 3723446000)))
    (is (= "...too much..." (size/bytes->readable 1723446000000)))))

;;;

(deftest readable->bytes--test
  (is (= 1 (size/readable->bytes "1b")))
  (is (= 1024 (size/readable->bytes "1kb")))
  (is (= 1024 (size/readable->bytes "1 KB")))
  (is (= 102400 (size/readable->bytes "100K")))
  (is (= 104857600 (size/readable->bytes "100M")))
  (is (= 524288000 (size/readable->bytes "500mb")))
  (is (= 5368709120 (size/readable->bytes "5G")))
  (is (= 1319413953331 (size/readable->bytes "1.2Tb"))))

(deftest readable->bytes--errors-test
  (is (nil? (size/readable->bytes "")))
  (is (nil? (size/readable->bytes "100")))
  (is (nil? (size/readable->bytes "GB")))
  (is (nil? (size/readable->bytes "1 Megabyte")))
  (is (nil? (size/readable->bytes "-100K")))
  (is (thrown? AssertionError (size/readable->bytes 1)))
  (is (thrown? AssertionError (size/readable->bytes nil)))
  (is (thrown? AssertionError (size/readable->bytes {}))))

(deftest readable->bytes--customizations-test
  (binding [size/*size-parseable-units* [#"(?i)byte" #"(?i)kilobyte" #"(?i)megabyte"]]
    (is (= 1 (size/readable->bytes "1 Byte")))
    (is (= 1024 (size/readable->bytes "1 Kilobyte")))
    (is (= 1048576 (size/readable->bytes "1 Megabyte")))
    (is (nil? (size/readable->bytes "1 Gigabyte")))))