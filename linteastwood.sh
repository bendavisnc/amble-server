clojure \
  -Sdeps '{:aliases
           {:eastwood
            {:extra-deps
             {jonase/eastwood {:mvn/version "1.4.3"}}
             :jvm-opts
             ["-Dport=3000"
              "-Dclient-url=http://localhost:8080"
              "-Dsqlite-db=amble-db/amble.db"
              "-Ddev-env=dev"
              "-Dclojure.compiler.warn-on-reflection=false"]
             :main-opts
             ["-m" "eastwood.lint"]}}}' \
  -M:eastwood