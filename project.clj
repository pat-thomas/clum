(defproject clum "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-auto      "0.1.3"]]
  :cljsbuild {:builds [{:source-paths ["src/clum/app"]
                        :compiler     {:output-to "public/js/app.js"
                                       :optimizations :whitespace
                                       :pretty-print  true}}]}
  :auto {:default {:file-pattern #"\.(clj)$"}}
  :source-paths ["src/clum/server"]
  :dependencies [;; server-side
                 [org.clojure/clojure       "1.8.0"]
                 [http-kit                  "2.2.0"]
                 [com.taoensso/timbre       "4.8.0"]
                 [overtone                  "0.10.1"]
                 [cheshire                  "5.7.0"]

                 ;; client-side
                 [reagent                    "0.6.0"]
                 [com.cognitect/transit-cljs "0.8.239"]])
