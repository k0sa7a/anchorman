(ns anchorman.api.gnews
  (:require [environ.core :refer [env]]
            [clj-http.client :as client]
            [cheshire.core :as json]))

(def gnews-api-key (env :gnews-api-key))



(defn get-article []
  (let [response-str (:body (client/get  (str "https://gnews.io/api/v4/top-headlines?category=science&lang=en&max=1&apikey=" gnews-api-key)))
        response (-> (json/parse-string response-str true)
                     :articles
                     first)
        title (:title response)
        description (:description response)
        content (:content response)
        source (:name (:source response))]
    ;; printing for tests/checks if needed
    ;; (prn "TITLE:\n")
    ;; (clojure.pprint/pprint title)
    ;; (print "----\n")
    ;; (prn "DESCRIPTION:\n")
    ;; (clojure.pprint/pprint description)
    ;; (print "----\n")
    ;; (prn "CONTENT:\n")
    ;; (clojure.pprint/pprint content)
    ;; (print "----\n")
    ;; (prn "SOURCE:\n")
    ;; (clojure.pprint/pprint source)
    ;; (print "----\n")
    response))

(get-article)
