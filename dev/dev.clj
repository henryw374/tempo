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
  (spit "web-target/public/browser-test/index.html"
    "<!DOCTYPE html>
    <html><head>
    <title>kaocha.cljs2.shadow-runner</title>
    <meta charset=\"utf-8\">
    </head>
    <body>
   
    <script src=\"/temporal.js\"></script>
    
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
  
  ; start up live-compilation of tests
  (test-watch)
  
  (refresh/clear)
  
  (capt/capt
    '(do
      (require '[com.widdindustries.gen.gen.tempo] :reload)
      (require '[com.widdindustries.gen.gen.accessors] :reload)
      (gen/gen-after)))
  
  (capt/exec)
  (gen/generate-all nil)
  (run-clj-tests nil)
  ; start a cljs repl session in the test build. :cljs/quit to exit
  (util/repl :browser-test-build)
  
  ; show what npm libs needed by cljs libs
  (util/show-npm-deps)
  
  ; do the release build
  (app-release)

  (util/build-report (app-config) "build-report.html")

  ; you can stop/start etc as required
  (util/stop-server)
  (util/clean-build)
  )

