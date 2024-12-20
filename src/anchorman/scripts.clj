(ns anchorman.scripts
  (:require [anchorman.api.gnews :as gnews]
            [anchorman.api.chatgpt :as chatgpt]
            [clojure.java.io :as io]))

(def article-to-use (gnews/get-article) )

(defn generate-script []
  (let [article article-to-use]

    (anchorman.api.chatgpt/ask-chatgpt-for-script article)))




(defn save-audio-to-file [binary-data file-path]
  (with-open [out (io/output-stream file-path)]
    (io/copy binary-data out)))

(defn save-text-to-file [string-data file-path]
  (with-open [out (io/output-stream file-path)]
    (.write out (.getBytes string-data "UTF-8"))))

(defn generate-audio []
  (let [script (generate-script)
        response (anchorman.api.chatgpt/ask-chatgpt-for-audio script)
        datetime (java.time.LocalDateTime/now)
        file-path (str "output/audio-" datetime ".mp3")
        file-path-script (str "output/script-" datetime ".txt")
        sanitized-timestamp-script (clojure.string/replace file-path-script #":" "-")
        sanitized-timestamp (clojure.string/replace file-path #":" "-")
        audio (:body response)]
    (print script)
    (save-text-to-file script sanitized-timestamp-script)
    (save-audio-to-file audio sanitized-timestamp)
    sanitized-timestamp))



(defn generate-caption []
   (let [article (gnews/get-article)
        caption-string (anchorman.api.chatgpt/ask-chatgpt-for-caption article)
        file-path (str "output/caption-" (java.time.LocalDateTime/now) ".txt")
        sanitized-timestamp (clojure.string/replace file-path #":" "-")]
    (save-text-to-file caption-string sanitized-timestamp)
    sanitized-timestamp)
  )
