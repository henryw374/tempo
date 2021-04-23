(ns com.widdindustries.gen.gen.tempo
  (:require [com.widdindustries.gen.gen.accessors :as accessors]
            [com.widdindustries.gen.gen.constructors :as constructors]
            [com.widdindustries.gen.gen :as gen]
            [clojure.string :as string]
            [clojure.java.io :as io]))

(defn ns-decl [feature]
  (rest (gen/read-cond-forms "dev/com/widdindustries/gen/gen_in/tempo.cljc"
          feature)))

(def feature->ext
  {:cljay ".clj"
   :cljs  ".cljs"
   :cljc  ".cljc"})

(defn gen-tempo [target feature]
  (let [deps (str "./gen-out/" target "/deps.edn")]
    (io/make-parents deps)
    (spit deps
      {:paths ["src"]}))
  (gen/gen (str "./gen-out/" target "/src/"
             (->
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

  (gen-tempo "no-deps" :cljay)

  )