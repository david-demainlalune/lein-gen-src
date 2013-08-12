(ns leiningen.parse-args)


(import 'java.io.File)

(def ^:dynamic *ns-root* "")

(defn split-on-dot
  [string]
  (filter (comp not empty?)
      (clojure.string/split string #"\.")))

(defn un-hyphenate
  [string] (clojure.string/replace string #"-" "_"))

(defn make-filename
  [a-name]
  (-> a-name
      split-on-dot
      last
      un-hyphenate))

(defn make-src-filename
  [a-name]
  (-> a-name
      make-filename
      (str ".clj")))


(defn make-test-filename
  [a-name]
  (-> a-name
      make-filename
      (str "_test.clj")))

(defn vec-to-path
  "helper function take a vector of string and joins them with File/separator"
  [vec-strings]
  (clojure.string/join File/separator
                       (filter (complement clojure.string/blank?) vec-strings)))



(defn extract-partial-ns
  [string]
  (let [vec-to-path-if-seq #(if (seq %) (vec-to-path %) "")]
    (-> string
        split-on-dot
        butlast
        vec-to-path-if-seq)))


(defn ns-from-string
  [string]
  (clojure.string/join "."
                       (cons *ns-root* (split-on-dot string))))


(defn parse-args [string]
  (let [src "src"
        test "test"
        project-ns (un-hyphenate *ns-root*)
        partial-ns (-> string extract-partial-ns un-hyphenate)
        src-filename (make-src-filename string)
        test-filename (make-test-filename string)]
    {:the-namespace (ns-from-string string)
     :src {:full-path (vec-to-path [src project-ns partial-ns src-filename])
           :dir-path (vec-to-path [src project-ns partial-ns])}
     :test {:full-path (vec-to-path [test project-ns partial-ns test-filename])
            :dir-path (vec-to-path [test project-ns partial-ns])}}))



; public function
(defn parse-args-with-project-ns
  [string
   project-ns]
  (binding [*ns-root* project-ns]
    (parse-args string)))