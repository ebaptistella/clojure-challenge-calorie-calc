(defproject calorie-calc "0.1.0-SNAPSHOT"
  :description "CLI system for nutritional data analysis"
  :url "https://github.com/example/calorie-calc"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/data.json "2.4.0"]
                 [generateme/fastmath "2.2.0"]
                 [org.clojure/tools.cli "1.0.206"]
                 [org.clojure/tools.namespace "1.5.0"]
                 [net.mikera/core.matrix "0.63.0"]]
  :main calorie-calc.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :dev {:dependencies [[org.clojure/tools.namespace "1.5.0"]]}}
  :aliases {"lint" ["run" "-m" "clojure-lsp.api" "diagnostics"]
            "lint-fix" ["run" "-m" "clojure-lsp.api" "clean-ns" "--dry"]})

