(ns compilation
  (:require [figwheel.main.api :as figwheel]
            [cljs.build.api :as cljs]
            [clojure.java.shell :as sh]))

(defn stop-live-compilation []
  (figwheel/stop-all))

(def debug-opts
  {:pseudo-names true
   :pretty-print true
   ;:source-map   true
   })

(defn clean []
  (sh/sh "rm" "-rf" "./web-target/public/cljs-out")
  (sh/sh "mkdir" "-p" "./web-target/public/cljs-out")
  )

(def common
  {})

(defn prod-build []
  (stop-live-compilation)
  (clean)
  (println "compiling...")
  (cljs/build
    (->
      {:optimizations :advanced
       :infer-externs true
       :main          'com.widdindustries.tempo
       :process-shim  false
       :output-dir    "web-target/public/cljs-out"
       :output-to     "web-target/public/cljs-out/main.js"}
      (merge common)
      ;(merge debug-opts)
      ))
  (println "done compiling"))

(defn start-live-compilation []
  (figwheel/start {:mode :serve}
    {:id      "dev"
     :options (merge common
                {:main         'com.widdindustries.tempo-test
                 :output-to    "web-target/public/cljs-out/main.js"
                 :output-dir   "web-target/public/cljs-out"
                 :repl-verbose false
                 :devcards true
                 })

     :config  {:auto-testing        true
               :open-url            false
               :watch-dirs          ["src" "test" ]
               ;:css-dirs            ["css"]
               :ring-server-options {:port 9503}
               }}))

(defn cljs-repl []
  (figwheel/cljs-repl "dev"))

(comment
  (clean)
  (start-live-compilation)
  (stop-live-compilation)
  (cljs-repl)
  (prod-build)

  )
