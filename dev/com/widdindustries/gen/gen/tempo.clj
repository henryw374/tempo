(ns com.widdindustries.gen.gen.tempo
  (:require [com.widdindustries.gen.gen.accessors :as accessors]
            [com.widdindustries.gen.gen.constructors :as constructors]
            [com.widdindustries.gen.gen :as gen]
            [clojure.string :as string]))

(defn ns-decl [feature]
  (rest (gen/read-cond-forms "dev/com/widdindustries/gen/gen_in/tempo.cljc"
          feature))
  #_(backtick/template
    `(ns com.widdindustries.tempo
      ""
     ~(when (= :cljay feature)
        (:import (java.time LocalDateTime ZonedDateTime)))
      ;(:require [com.widdindustries.tempo.js-temporal-entities :as entities])
      ))
  )

(ns-decl :cljay)

(def feature->ext
  {:cljay ".clj"
   :cljs ".cljs"
   :cljc ".cljc"})

(defn gen-tempo [feature]
  (gen/gen (str "./gen-out/" (->
                               (name 'com.widdindustries.tempo)
                               (string/replace "." "/")
                               (string/replace "-" "_")
                               )
             (get feature->ext feature))
    (concat (ns-decl feature)
      (accessors/accessor-forms feature)
      (constructors/constructor-fns feature)
      )))


(comment

  (gen-tempo :cljay)

  )
