(ns leiningen.file-handling-test
  (:use clojure.test
        leiningen.file-handling))


(import 'java.io.File)

(def separator File/separator)

(def test-path-map
  {:the-namespace "",
   :src {:full-path (str "resources" separator "src" separator "test.clj"),
                         :dir-path (str "resources" separator "src")},
   :test {:full-path (str "resources" separator "test" separator "test.clj"),
                         :dir-path (str "resources" separator "test")}})

(def test-path-map-2
  {:the-namespace "",
   :src {:full-path (str "resources" separator "src" separator "IDontExist.clj"),
                         :dir-path (str "resources" separator "foo")},
   :test {:full-path (str "resources" separator "test" separator "IDontExist.clj"),
                         :dir-path (str "resources" separator "foo")}})


(deftest test-accum-path
  (testing "it should return an empty seq if given an empty seq given"
    (is (= (accum-path [])
           [])))

  (testing "it should return identity if given a vec of one string"
    (is (= (accum-path ["foo"])
           ["foo"])))

  (testing "it should return a walk of path given a vec of multiple strings"
    (is (= (accum-path ["foo" "bar" "pop"])
           ["foo" (str "foo" separator "bar") (str "foo" separator "bar" separator "pop")])))
  )



(deftest test-validate-path-map
  ; this test relies on existence of 2 files
  ; resources/src/test.clj
  ; resources/test/test.clj
  (testing "it should detect when full-path points to an existing file"
    (is (= (map :valid (validate-path-map test-path-map))
           [false false true true])))

  ; this test relies on existence of 1 file
  ; resources/foo
  (testing "it should detect when an element of dir-path is to an existing file"
    (is (= (map :valid (validate-path-map test-path-map-2))
           [true true false false]))))