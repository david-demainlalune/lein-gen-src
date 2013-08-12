
(ns leiningen.parse-args-test
  (:use clojure.test
        leiningen.parse-args))



(deftest test-extract-partial-ns
  (testing "it returns empty string if no partial ns in string"
    (is (= (extract-partial-ns "a-name")
           "")))

  (testing "it returns the partial ns as a path if it exists"
    (is (= (extract-partial-ns "a-namespace.a-file")
           (vec-to-path ["a-namespace"]))))

  (testing "it returns the partial ns as a path if it exists however deep"
    (is (= (extract-partial-ns "a-namespace.another-ns.a-file")
           (vec-to-path ["a-namespace" "another-ns"]))))

  (testing "it handles dot on start and end"
    (is (= (extract-partial-ns ".another-ns.a-file.")
           (vec-to-path ["another-ns"])))))





(deftest test-parse-args
  (testing "function takes a string
    and returns a map with dirpaths and
    fullpaths attributes for src and test"
    (is (= (parse-args "filename")
           {:the-namespace (str *ns-root* "." "filename")
            :src {:full-path (vec-to-path ["src" *ns-root* "filename.clj"])
                  :dir-path (vec-to-path ["src" *ns-root*])}
            :test {:full-path (vec-to-path ["test" *ns-root* "filename_test.clj" ])
                   :dir-path (vec-to-path ["test" *ns-root*])}})))


  (testing "it support hyphenated names"
    (is (= (parse-args "just-filename")
           {:the-namespace (str *ns-root* "." "just-filename")
            :src {:full-path (vec-to-path ["src" *ns-root* "just_filename.clj"])
                  :dir-path (vec-to-path ["src" *ns-root*])}
            :test {:full-path (vec-to-path ["test" *ns-root* "just_filename_test.clj" ])
                   :dir-path (vec-to-path ["test" *ns-root*])}})))


  (testing "it supports partially qualified namespaces"
    (is (= (parse-args "some-sub-ns.just-filename")
           {:the-namespace (str *ns-root* ".some-sub-ns.just-filename")
            :src {:full-path (vec-to-path ["src" *ns-root* "some_sub_ns" "just_filename.clj"])
                  :dir-path (vec-to-path ["src" *ns-root* "some_sub_ns"])}
            :test {:full-path (vec-to-path ["test" *ns-root* "some_sub_ns" "just_filename_test.clj" ])
                   :dir-path (vec-to-path ["test" *ns-root* "some_sub_ns"])}})))
  )