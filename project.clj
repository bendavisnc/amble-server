(defproject amble-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [compojure "1.6.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]
                 [info.sunng/ring-jetty9-adapter "0.14.2"]
                 [org.clojure/data.json "1.0.0"]
                 [org.clojure/core.async "1.3.610"]
                 [ring-logger "1.1.1"]
                 [bananaoomarang/ring-debug-logging "1.1.0"]
                 [org.apache.logging.log4j/log4j-api "2.14.0"]
                 [org.apache.logging.log4j/log4j-core "2.14.0"]
                 [org.clojure/java.jdbc "0.7.8"]
                 [yesql "0.5.3"]
                 [environ "1.2.0"]
                 [org.xerial/sqlite-jdbc "3.36.0.3"]
                 [org.clojure/core.async "1.5.648"]]




  :plugins [[lein-ring "0.12.5"]
            [lein-auto "0.1.3"]
            [lein-cljfmt "0.7.0"]
            [lein-environ "1.2.0"]]

  :main amble-server.main
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]
                        [ring/ring-devel "1.9.4"]]
                       :jvm-opts ["-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"]
         :env {:port 3000}}} 
  :java-source-paths ["src-java"]
  :javac-options     ["-target" "1.8" "-source" "1.8"])
