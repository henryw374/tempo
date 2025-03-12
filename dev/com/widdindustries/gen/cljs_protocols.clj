(ns com.widdindustries.gen.cljs-protocols
  (:require [com.widdindustries.gen.graph :as graph]
            [com.widdindustries.gen.gen :as gen]
            [backtick]
            [clojure.string :as string]))

(defn ns-decl []
  (backtick/template
    (ns com.widdindustries.tempo.cljs-protocols
      ""
      (:refer-clojure :exclude [time])
      (:require [com.widdindustries.tempo.js-temporal-entities :as entities])))
  )

(defn non-temporals []
  (backtick/template
    (
      (defn duration []
        (extend-protocol IHash
          js/Temporal.Duration
          (-hash [o] (hash (str o))))

        (extend-protocol IEquiv
          js/Temporal.Duration
          (-equiv [o other] (zero? (compare o other))))

        (extend-protocol IComparable
          js/Temporal.Duration
          (-compare [x y] (js/Temporal.Duration.compare ^js x y))))

      #_(defn timezone []
        (extend-protocol IEquiv
          js/Temporal.TimeZone
          (-equiv [o other] (= (.-id ^js o) (.-id ^js other))))

        (extend-protocol IHash
          js/Temporal.TimeZone
          (-hash [o] (hash (.-id ^js o))))))))

(defn temporal-fn-gen [temporal-type overrides]
  (let [compar (or (get overrides :compare)
                 (backtick/template (-compare [x y] (.compare ~(symbol "entities" temporal-type) ^js x y))))]
    (backtick/template
      (defn ~(symbol temporal-type) []
        (extend-protocol IEquiv
          ~(symbol "entities" temporal-type)
          (-equiv [o other] (.equals ^js o other)))

        (extend-protocol IHash
          ~(symbol "entities" temporal-type)
          (-hash [o] (hash (str o))))

        (extend-protocol IComparable
          ~(symbol "entities" temporal-type)
          ~compar)))))

(defn extend-all [entities]
  (backtick/template
    (defn extend-all []
      ~@(map 
          (fn [e] (list e))
          entities))
    ))

(def overrides 
  {'monthday {:compare (backtick/template
                         (-compare [^js x ^js y]
                           (let [m (compare (.-monthCode x) (.-monthCode y))]
                             (if (zero? m)
                               (compare (.-day x) (.-day y))
                               m))))}})

(defn gen-protocols []
  (gen/gen (str "src/" (->
                         (name 'com.widdindustries.tempo.cljs-protocols)
                         (string/replace "." "/")
                         (string/replace "-" "_")
                         )
             ".cljs")
    (concat [(ns-decl)]
      (non-temporals)
      (for [tt graph/temporal-types]
        (temporal-fn-gen (str tt) (get overrides tt)))
      [(extend-all (concat graph/temporal-types ['timezone 'duration]))])))
