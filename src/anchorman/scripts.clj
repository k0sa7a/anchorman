(ns anchorman.scripts
  (:require [anchorman.api.gnews :as gnews]
            [anchorman.api.chatgpt :as chatgpt]
            [clojure.java.io :as io]))


(defn generate-script []
  (let [article (gnews/get-article)]

    (anchorman.api.chatgpt/ask-chatgpt-for-script article)))




(defn save-audio-to-file [binary-data file-path]
  (with-open [out (io/output-stream file-path)]
    (io/copy binary-data out)))


(defn generate-audio []
  (let [script (generate-script)
        response (anchorman.api.chatgpt/ask-chatgpt-for-audio script)
        file-path (str "output/audio-" (java.time.LocalDateTime/now) ".mp3")
        sanitized-timestamp (clojure.string/replace file-path #":" "-")
        audio (:body response)]

    (save-audio-to-file audio sanitized-timestamp)
    sanitized-timestamp))



(generate-audio)
