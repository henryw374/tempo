(ns com.widdindustries.gen.gen.constructors
  (:require [com.widdindustries.gen.gen :as gen]))

(defn constructor-fns [feature]
  (rest (gen/read-cond-forms "gen_in/constructors.cljc"
          feature)))

(comment
  (constructor-fns #{:cljc})
  (constructor-fns #{:cljay})
  (constructor-fns #{:cljs})
  

  )
