(ns com.widdindustries.gen.gen.tempo
  (:require [com.widdindustries.gen.gen.accessors :as accessors]
            [com.widdindustries.gen.gen.constructors :as constructors]
            [com.widdindustries.gen.gen :as gen]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.set :as set]))

(defn ns-decl [features]
  (rest (gen/read-cond-forms "dev/com/widdindustries/gen/gen_in/tempo.cljc"
          features)))

(defn feature->ext [features]
  (condp set/subset? features
    #{:cljay} ".clj"
    #{:cljs} ".cljs"
    #{:cljcc} ".clj"
    #{:cljcs} ".cljs"
    ))

;(feature->ext #{:cljcs :cljc})

(defn gen-tempo [target features main-feature]
  (let [deps (str "./gen-out/" target "/deps.edn")]
    (io/make-parents deps)
    (spit deps
      (merge
        (when (contains? features :cljc)
          {:deps {'cljc.java-time {:mvn/version "0.1.16"}}})
        {:paths ["src"]})))
  (gen/gen (str "./gen-out/" target "/src/"
             (->
               (name 'com.widdindustries.tempo)
               (string/replace "." "/")
               (string/replace "-" "_")
               )
             (feature->ext features))
    (concat (ns-decl features)
      ['(comment "accessors")]
      (accessors/accessor-forms main-feature)
      ['(comment "parsers")]
      (accessors/parse-forms main-feature)
      ['(comment "nowers")]
      (accessors/now-forms main-feature)
      ['(comment "constructors")]
      (constructors/constructor-fns features)
      )))


(comment

  (gen-tempo "no-deps" #{:cljay} :cljay)
  (gen-tempo "cljc.java-time-dep" #{:cljc :cljcc} :cljc)
  (gen-tempo "cljc.java-time-dep" #{:cljc :cljcs} :cljc)

  )
