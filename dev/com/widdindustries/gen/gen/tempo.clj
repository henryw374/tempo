(ns com.widdindustries.gen.gen.tempo
  (:require [com.widdindustries.gen.gen.accessors :as accessors]
            [com.widdindustries.gen.gen.constructors :as constructors]
            [com.widdindustries.gen.gen :as gen]
            [com.widdindustries.gen.cljs-protocols :as cljs-protocols]
            [clojure.string :as string]
            [clojure.tools.namespace.repl]
            [clojure.java.io :as io]
            [clojure.set :as set]))

(defn non-graph [feature]
  (let [forms (rest (gen/read-cond-forms "gen_in/tempo.cljc"
                      #{feature}))
        per-graph (->> forms
                       (take-while #(not= '(comment "after-graph") %)))]
    [per-graph (drop (count per-graph) forms)]))

(defn feature->ext [feature]
  (case feature
    :cljay ".clj"
     :cljs ".cljs"))

;(feature->ext #{:cljcs :cljc})

(defn gen-tempo [  main-feature]
  (let [[pre-graph post-graph] (non-graph main-feature)]
    (gen/gen (str "src/com/widdindustries/tempo"
               (feature->ext main-feature))
      (concat
        pre-graph
        ['(comment "accessors")]
        (accessors/accessor-forms main-feature)
        ['(comment "parsers")]
        (accessors/parse-forms main-feature)
        ['(comment "nowers")]
        (accessors/deref-forms main-feature)
        ['(comment "constructors")]
        (constructors/constructor-fns main-feature)
        ['(comment "other")]
        post-graph
        )))

  )

(defn generate-test []
  (gen/gen (str  "test/com/widdindustries/tempo_gen_test.cljc"
             )
    (concat ['(ns com.widdindustries.tempo-gen-test
                (:require [clojure.test :refer [deftest is testing]]
                          [com.widdindustries.tempo :as t]))]
      ['(t/extend-all-cljs-protocols)]
      ;['(comment "constructors")]
      ;(constructors/constructor-tests features)
      ['(comment "accessors")]
      ;(accessors/accessor-forms main-feature)
      ['(comment "parsers")]
      (accessors/parse-tests)
      ['(comment "derefs")]
      (accessors/deref-tests )
      ['(comment "accessors")]
      (accessors/accessor-tests )

      )))

(defn gen-after []
  (gen-tempo :cljay)
  (gen-tempo :cljs)
  (cljs-protocols/gen-protocols)
  (generate-test))

(defn generate-all [_]
  (clojure.tools.namespace.repl/set-refresh-dirs "dev")
  (clojure.tools.namespace.repl/clear)
  (clojure.tools.namespace.repl/refresh :after `gen-after)
  )

(comment
(generate-all nil)
  ;(accessors/accessor-forms :cljs)
  (gen-tempo "no-deps" #{:cljay} :cljay)
  (gen-tempo "no-deps" #{:cljs} :cljs)
  ;(gen-tempo "cljc.java-time-dep" #{:cljc :cljcc} :cljc)
  ;(gen-tempo "cljc.java-time-dep" #{:cljc :cljcs} :cljc)

  )
