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

(defn read-cond-forms [f features]
  (with-open [r (java.io.PushbackReader. (clojure.java.io/reader f))]
    (binding [*read-eval* false]
      (->>
        (repeatedly #(try (read
                            {:read-cond :allow
                             :features  features
                             :eof       ::EOF} r)
                          (catch Error e
                            (println f)
                            (throw (ex-info (str f) {} e)))))
        (take-while #(not= ::EOF %))
        vec))))
