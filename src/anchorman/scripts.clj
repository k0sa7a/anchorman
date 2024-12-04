(ns anchorman.scripts
  (:require [anchorman.api.gnews :as gnews]
            [anchorman.api.chatgpt :as chatgpt]))


(defn generate-script []
  (let [article (gnews/get-article)]
    (anchorman.api.chatgpt/ask-chatgpt-for-script article)))

(generate-script)
