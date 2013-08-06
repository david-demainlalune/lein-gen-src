(ns leiningen.gen-src)

(import 'java.io.File)
(import 'java.io.IOException)

(def ^:dynamic *ns-root* nil)

(defn make-file
  [path]
  (File. path))


(defn source-template
  [full-namespace]
  (format "(ns %s)" full-namespace))


(defn test-template
  [full-namespace]
  (format "(ns %s-test
          (:use clojure-test
          %s))" full-namespace full-namespace))

; takes a vec of paths [a b c] and returs a/b/c/
; returns "" when input is empty
(defmulti make-path (fn [vec-dir-names]
                      (if (empty? vec-dir-names) :no-dir-names  :dirs)))

(defmethod make-path :dirs
  [vec-dir-names]
  (str
   (clojure.string/join File/separator vec-dir-names)
   File/separator))

(defmethod make-path :no-dir-names [_] "")


(defn make-dirs
  [path-string]
  (-> path-string
      make-file
      .mkdirs))


(defn write-file
  [vec-dir-names file-name content]
  (let [path-string (make-path vec-dir-names)]
    (make-dirs path-string)
    (spit (str path-string file-name) content)))

(defn write-source-and-test-files
  [vec-dir-names base-name src-namespace]
  (let [src-file-name (str base-name ".clj")
        test-file-name (str base-name "_test.clj")]

    (write-file (concat (vector "src" *ns-root*) vec-dir-names)
                src-file-name
                (source-template src-namespace))

    (write-file (concat (vector "test" *ns-root*) vec-dir-names)
                test-file-name
                (test-template src-namespace))))



(defn split-on-namespace
  "takes string and splits on delimeter
  removes empty strings from result"
  [string]
  (filter (comp not empty?)
      (clojure.string/split string #"\.")))


(defn generate-files
  [name]
  (let [fully-qualified-src-ns (str *ns-root* "." name)
        splits (split-on-namespace name)
        base-name (last splits)
        dir-names (butlast splits)]
    (write-source-and-test-files dir-names base-name fully-qualified-src-ns)))


(defmulti generate (fn
                     [project args]
                     (cond
                      (nil? (:root project)) :not-in-project-context
                      (empty? args) :no-args
                       :else :args-received)))
(defmethod generate
  :not-in-project-context
  [_ _]
  (println "this task must be run in context of a project"))


(defmethod generate
  :no-args
  [_ _]
  (println "no arguments given"))

(defmethod generate
  :args-received
  [project args]
  (binding [*ns-root* (:name project)]
    (println (str "running in project -> " *ns-root*))
    (doseq [n args]
      (generate-files n))))


(defn gen-src
  "generate src and test file in current namespace"
  [project & args]
  (generate project args))
