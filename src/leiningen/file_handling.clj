(ns leiningen.file-handling)


(import 'java.io.File)
(import 'java.io.IOException)

(defn make-file
  [path]
  (File. path))


(defn is-file?
  [file]
  (.isFile file))

(defn is-directory?
  [file]
  (.isDirectory file))

(defn is-path-file-or-directory?
  [path]
  (let [file (make-file path)]
    (or (is-directory? file) (is-file? file))))


(defn accum-path
  "takes [foo bar pof] returns [foo foo/bar foo/bar/pof]"
  [split-path]
  (if (seq split-path)
    (loop [result [], source (rest split-path), current-value (first split-path)]
      (if (seq source)
        (recur (conj result current-value)
               (rest source)
               (str current-value File/separator (first source)))
        ;else add last element
        (conj result current-value)))
    ; else return empty seq
    (empty split-path)))

(defn split-path
  [path]
  (clojure.string/split path (re-pattern
                              (java.util.regex.Pattern/quote File/separator))))

(defn is-part-of-path-an-existing-file?
  [dir-path]
  (->> dir-path
       split-path
       accum-path
       (map make-file)
       (map is-file?)
       (some true?)))



(defn make-dirs
  [path-string]
  (-> path-string
      make-file
      .mkdirs))


(defn build-validations-vector
  "takes a path map
  returns a vec of validation vecs [validator-fn value invalid-message]"
  [{:keys [src test] :as path-map}]
  (let [full-path-src (:full-path src)
        full-path-test (:full-path test)
        dir-path-src (:dir-path src)
        dir-path-test (:dir-path test)]
    [[(complement is-path-file-or-directory?),
      full-path-src,
      (format "file %s already exists" full-path-src) ]
     [(complement is-path-file-or-directory?),
      full-path-test,
      (format "file %s already exists" full-path-test)]
     [(complement is-part-of-path-an-existing-file?),
      dir-path-src,
      (format "path %s attempts to overwrite an existing file" dir-path-src)]
     [(complement is-part-of-path-an-existing-file?),
      dir-path-test,
      (format "path %s attempts to overwrite an existing file" dir-path-test)]]))

(defn validate
  [[validator-fn value invalid-message]]
  (if (validator-fn value)
    {:valid true
     :value value}
    {:valid false
     :value value
     :message invalid-message}))


(defn validate-path-map
  [{:keys [src test] :as path-map}]
  (->> path-map
       build-validations-vector
       (map validate)))

(defn write-src-and-test
  [{:keys [src test] :as path-map} src-content test-content]
  (do
    (make-dirs (:dir-path src)) ; TODO check return value of make-dirs
    (spit (:full-path src) src-content)

    (make-dirs (:dir-path test))
    (spit (:full-path test) test-content)

    ; return value
    (format "succesfully written source and test files\n %s and %s\n"
            (:full-path src) (:full-path test))
  ))




; public method
(defn write-files
  [{:keys [src test] :as path-map} src-content test-content]
  (let [failed-validations (filter (complement :valid)
                                   (validate-path-map path-map))]
    (if (seq failed-validations)
      (str "operation aborted\n"
           (clojure.string/join "\n" (map :message failed-validations)))
      ; else
      (write-src-and-test path-map src-content test-content))))
