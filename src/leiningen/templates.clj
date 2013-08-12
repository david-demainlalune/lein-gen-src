(ns leiningen.templates)


(defn source-template
  [a-namespace]
  (format "(ns %s)" a-namespace))


(defn test-template
  [a-namespace]
  (format "(ns %s-test
          (:use clojure-test
          %s))" a-namespace a-namespace))

