(ns com.widdindustries.gen.gen.tempo
  (:require [com.widdindustries.gen.gen.accessors :as accessors]
            [com.widdindustries.gen.gen.constructors :as constructors]
            [com.widdindustries.gen.gen :as gen]
            [com.widdindustries.gen.cljs-protocols :as cljs-protocols]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.set :as set]))

(defn ns-decl [features]
  (rest (gen/read-cond-forms "gen_in/tempo.cljc"
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
  
  (gen/gen (str  "src/com/widdindustries/tempo" (when (= "cljc.java-time-dep" target)
                                                   "/cljc-java-time-dep")
             
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

(defn generate-all [_]
  (gen-tempo "no-deps" #{:cljay} :cljay)
  (gen-tempo "no-deps" #{:cljs} :cljs)
  (cljs-protocols/gen-protocols))

(comment
(generate-all nil)
  ;(accessors/accessor-forms :cljs)
  (gen-tempo "no-deps" #{:cljay} :cljay)
  (gen-tempo "no-deps" #{:cljs} :cljs)
  ;(gen-tempo "cljc.java-time-dep" #{:cljc :cljcc} :cljc)
  ;(gen-tempo "cljc.java-time-dep" #{:cljc :cljcs} :cljc)

  )
