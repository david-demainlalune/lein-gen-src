(ns leiningen.gen-src
  (:require [leiningen.parse-args :as parse-args]
            [leiningen.file-handling :as file-handling]
            [leiningen.templates :as templates]))




(defmulti handle-args (fn
                        [project args]
                        (cond
                         (nil? (:root project)) :not-in-project-context
                         (empty? args) :no-args
                         :else :args-received)))
(defmethod handle-args
  :not-in-project-context
  [_ _]
  (println "this task must be run in context of a project"))


(defmethod handle-args
  :no-args
  [_ _]
  (println "no arguments given"))

(defmethod handle-args
  :args-received
  [project args]
  (let [project-name (:name project)
        write-all (fn [{:keys [the-namespace] :as path-map}]
                    (file-handling/write-files path-map
                                            (templates/source-template the-namespace)
                                            (templates/test-template the-namespace)))]
    (doseq [arg args]
      (-> arg
          (parse-args/parse-args-with-project-ns project-name)
          write-all
          println))))



(defn gen-src
  "generate src and test file in current namespace"
  [project & args]
  (handle-args project args))
