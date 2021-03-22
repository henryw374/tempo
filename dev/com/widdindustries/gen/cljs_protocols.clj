(ns com.widdindustries.gen.cljs-protocols
  (:require [com.widdindustries.gen.graph :as graph]))

(defn ns-decl []
  `(ns com.widdindustries.gen.cljs-protocols
     ""
     (:require [com.widdindustries.tempo.js-temporal-entities :as entities]))
  )

(defn temporal-fn-gen [temporal-type]
  `(defn plain-date []
     (extend-protocol IEquiv
       ~(symbol "entities" temporal-type)
       (-equiv [o other] (.equals ^js o other)))

     (extend-protocol IHash
       ~(symbol "entities" temporal-type)
       (-hash [o] (hash (str o))))

     (extend-protocol IComparable
       ~(symbol "entities" temporal-type)
       (-compare [x y] (.compare ^js x y)))))

(comment

  (doseq [tt graph/temporal-types]
    (temporal-fn-gen (str tt)))
  )
