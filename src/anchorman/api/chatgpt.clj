(ns anchorman.api.chatgpt
  (:require [environ.core :refer [env]]
            [clj-http.client :as client]
            [cheshire.core :as json]))

(def chatgpt-api-key (env :chatgpt-api-key))



(defn generate-payload [article]

  (let [title (:title article)
        description (:description article)
        source (:name (:source article))
        content (:content article)
        clj-payload {"model" "gpt-4o-mini"
                     "messages" [{"role" "system"
                                  "content" "You are a professional news anchor named News Bot. Your task is to deliver engaging scripts for news segments.
"
                                  }
                                 {"role" "user"
                                  "content" (format "You will receive the title, description, source, and content of a news article. Based on this information:
                                                       - Write a professional and engaging script as if delivering it on air.
                                                       - Limit the script to approximately 150-200 words (about 1-minute reading time).
                                                       - Do not include any additional text or explanation; output only the script.
                                                       - Do not include any placeholders of any kind, just text as it is to be read.
                                                       - Do not include reference to the time of day when the news are being presented.
                                                       - Reference the source of the news.
                                                     Title: %s\n
                                                     Description: %s\n
                                                     Source: %s\n
                                                     Content: %s" title description source content)}]}]
    (json/generate-string clj-payload)))

(defn ask-chatgpt-for-script [article]
  (let [payload (generate-payload article)
        response-str (:body (client/post "https://api.openai.com/v1/chat/completions" {:headers {"Authorization" (str "Bearer " chatgpt-api-key)
                                                                                                 "Content-Type" "application/json"}
                                                                                       :body payload}))
        response (json/parse-string response-str true)
        script (-> response
                   :choices
                   first
                   :message
                   :content)]
    script)
  )
