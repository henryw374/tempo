(ns com.widdindustries.gen.gen
  (:require [clojure.java.io :as io]))

(defn gen [path forms]
  (let [f path
        _ (io/make-parents f)
        w (io/writer f)]
    (binding [*print-meta* true
              *out* w]
      (doseq [e forms]
        (clojure.pprint/pprint e)
        (println)
        ))))
