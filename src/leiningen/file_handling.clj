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

(defn is-file-or-directory?
  [file]
  (or (is-directory? file) (is-file? file)))

(defn make-dirs
  [path-string]
  (-> path-string
      make-file
      .mkdirs))



(defmulti write-src-and-test (fn [{:keys [src test] :as path-map} _ _]
                               (vector (:file-exists? src) (:file-exists? test))))


(defmethod write-src-and-test
  [true true]
  [{:keys [src test] :as path-map} _ _]
  (format "operation aborted\nboth source and test files already exist\n%s\n%s\n"
          (:full-path src)
          (:full-path test)))

(defmethod write-src-and-test
  [true false]
  [{:keys [src test] :as path-map} _ _]
  (format "operation aborted\nsource file %s already exists\n"
          (:full-path src)))

(defmethod write-src-and-test
  [false true]
  [{:keys [src test] :as path-map} _ _]
  (format "operation aborted\ntest file %s already exists\n"
          (:full-path test)))

(defmethod write-src-and-test
  [false false]
  [{:keys [src test] :as path-map} src-content test-content]
  (do
    (make-dirs (:dir-path src))
    (spit (:full-path src) src-content)

    (make-dirs (:dir-path test))
    (spit (:full-path test) test-content)
    (format "succesfully written source and test files\n %s and %s\n"
            (:full-path src) (:full-path test))
  ))

(defn add-file-exists-attr
  [{:keys [the-namespace src test] :as path-map}]
  {:the-namespace the-namespace
   :src (merge src {:file-exists? (is-file-or-directory? (-> src :full-path make-file))})
   :test (merge test {:file-exists? (is-file-or-directory? (-> test :full-path make-file))})})


; public method
(defn write-files
  [{:keys [src test] :as path-map} src-content test-content]
  (-> path-map
      add-file-exists-attr
      (write-src-and-test src-content test-content)))