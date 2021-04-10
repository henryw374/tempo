(ns com.widdindustries.gen.gen.accessors
  (:require [com.widdindustries.gen.graph :as graph]
            [clojure.walk]))

(def all-paths (let [paths (atom [])]
                 (clojure.walk/postwalk
                   (fn [node]
                     (when-let [pathz (:paths node)]
                       (swap! paths concat pathz)
                       )
                     node)
                   graph/with-paths)
                 @paths))



(comment
 (loop [[next-path & remaining] all-paths
        access-paths #{}]
   (if-not next-path
     access-paths
     ()
     )
   )
  (count )
  

  )
