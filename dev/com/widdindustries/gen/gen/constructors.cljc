(ns com.widdindustries.gen.gen.constructors
  (:require [com.widdindustries.gen.gen :as gen]))

(defn constructor-fns [feature]
  (rest (gen/read-cond-forms "dev/com/widdindustries/gen/gen_in/constructors.cljc"
          feature)))

(comment
  (constructor-fns :cljc)
  

  (with-open [r (java.io.PushbackReader. (clojure.java.io/reader "dev/com/widdindustries/gen/gen_in/constructors.cljc"))]
    (binding [*read-eval* false]
      (->>
        (repeatedly #(read
                       {:read-cond :allow
                        :features  #{:cljay}
                        :eof       ::EOF} r))
        (take-while #(not= ::EOF %))
        vec)))

  (read-string {:read-cond :allow :features #{:cljs}}
    ())

  )
