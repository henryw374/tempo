(ns dev
  (:require [clojure.java.io :as io]
            [com.widdindustries.tiado-cljs2 :as util]
            [widdindustries.capture :as capt]
            [kaocha.repl :as kaocha]
            [babashka.fs :as fs]
            [clojure.tools.namespace.repl :as refresh]
            [com.widdindustries.gen.gen.tempo :as gen]))

(defn refresh []
  (refresh/refresh-all))

(defn x []
  (kaocha/run 'com.widdindustries.tempo-test))

(defn run-clj-tests [_]
  (refresh/refresh-all :after 'dev/x))

(defn browser-test-build [compile-mode opts]
  ((get util/compile-fns compile-mode)
   (util/browser-test-config) opts)
  (.mkdirs (io/file "web-target" "public" "browser-test"))
  ; https://esm.sh/temporal-polyfill@0.2.3
  (spit "web-target/public/browser-test/index.html"
    "<!DOCTYPE html>
    <html><head>
    <title>kaocha.cljs2.shadow-runner</title>
    <meta charset=\"utf-8\">
    </head>
    <body>
   
    <script>
        if(!window.Temporal){
          document.write('<script src=\"https://cdn.jsdelivr.net/npm/temporal-polyfill@0.2.3/global.min.js\"><\\/script>');         
                  }
    </script>

   
    <script src=\"/browser-test/js/test.js\">
    </script>
    <script>kaocha.cljs2.shadow_runner.init();</script></body></html>"
    ))

(defn test-watch []
  (browser-test-build :watch {}))

(defn tests-ci-shadow [{:keys [compile-mode]}]
  (util/start-server)
  (browser-test-build compile-mode {})
  (try
    (util/kaocha-exit-if-fail (util/run-tests-headless nil))
    (catch Exception e
      (println e)
      (System/exit 1))))

(comment
  (util/stop-server)
  ; start up live-compilation of tests
  (test-watch)
  
  (refresh/clear)
  
  (capt/capt
    '(do
      (require '[com.widdindustries.gen.gen.tempo] :reload)
      (require '[com.widdindustries.gen.gen.accessors] :reload-all)
      (gen/gen-after)))
  
  (capt/exec)
  (gen/generate-all nil)
  (run-clj-tests nil)
  ; start a cljs repl session in the test build. :cljs/quit to exit
  (util/repl :browser-test-build)
  
  ; show what npm libs needed by cljs libs
  (util/show-npm-deps)
  
 
  ; you can stop/start etc as required
  (util/stop-server)
  (util/clean-build)
  )

(defn app-config []
  (->
    (util/browser-app-config)
    (merge
      {:modules          {:main {:entries ['noodle.client-app]}}})))

(defn app-release []
  (util/prod-build
    (-> (app-config)
        (assoc :compiler-options {:pretty-print true
                                  :pseudo-names true})
        (dissoc :devtools))))

(comment

  ; do the release build
  (widdindustries.capture/capt
    '(do (app-release)
         (-> (fs/file "web-target/public/cljs-out/main.js")
                        (.length)
             (- 518559))))

  (util/build-report (app-config) "build-report.html")

  )