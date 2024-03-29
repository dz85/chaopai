(defproject
  chaopai
  "0.1.0-SNAPSHOT"
  :dependencies
  [[org.clojure/clojure "1.5.1"]
   [lib-noir "0.4.9"]
   [compojure "1.1.5"]
   [org.clojure/data.json "0.2.1"]
   [cheshire "5.0.2"]
   [pghstore-clj "0.1.0"]
   [ring-server "0.2.7"]
   [com.taoensso/timbre "1.5.2"]
   [com.taoensso/tower "1.2.0"]
   [markdown-clj "0.9.19"]
   [org.clojure/java.jdbc "0.2.3"]
   [org.clojars.kbarber/postgresql "9.2-1002.jdbc4"]
   [korma "0.3.0-RC2"]
   [log4j
    "1.2.15"
    :exclusions
    [javax.mail/mail
     javax.jms/jms
     com.sun.jdmk/jmxtools
     com.sun.jmx/jmxri]]
   [clabango "0.5"]]
  :ring
  {:handler chaopai.handler/war-handler,
   :init chaopai.handler/init,
   :destroy chaopai.handler/destroy}
  :profiles
  {:production
   {:ring
    {:open-browser? false, :stacktraces? false, :auto-reload? false}},
   :dev
   {:dependencies [[ring-mock "0.1.3"] [ring/ring-devel "1.1.8"]]}}
  :url
  "http://example.com/FIXME"
  :plugins
  [[lein-ring "0.8.3"]]
  :description
  "FIXME: write description"
  :min-lein-version "2.0.0")
