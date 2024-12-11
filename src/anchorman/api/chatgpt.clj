(ns anchorman.api.chatgpt
  (:require [environ.core :refer [env]]
            [clj-http.client :as client]
            [cheshire.core :as json]))

(def chatgpt-api-key (env :chatgpt-api-key))



(defn script-generate-payload [article]

  (let [title (:title article)
        description (:description article)
        source (:name (:source article))
        content (:content article)
        clj-payload {"model" "gpt-4o-mini"
                     "messages" [{"role" "system"
                                  "content" "You are a professional news anchor named Nexus at The Cyber Bulletin. Your task is to deliver engaging scripts for news segments."
                                  }
                                 {"role" "user"
                                  "content" (format "You will receive the title, description, source, and content of a news article. Based on this information:
                                                       - Write a professional and engaging script as if delivering it on air.
                                                       - Reference the source for the news.
                                                       - Limit the script to approximately 140 words (around 50 seconds reading time).
                                                       - Do not include any additional text or explanation; output only the script.
                                                       - Do not include any placeholders of any kind, just text as it is to be read.
                                                       - Do not include reference to the time of day when the news are being presented.
                                                       - Mention your name (Nexus) and The Cyber Bulletin
                                                     Title: %s\n
                                                     Description: %s\n
                                                     Source: %s\n
                                                     Content: %s" title description source content)}]}]
    (json/generate-string clj-payload)))

(defn ask-chatgpt-for-script [article]
  (let [payload (script-generate-payload article)
        response-str (:body (client/post "https://api.openai.com/v1/chat/completions" {:headers {"Authorization" (str "Bearer " chatgpt-api-key)
                                                                                                 "Content-Type" "application/json"}
                                                                                       :body payload}))
        response (json/parse-string response-str true)
        script (-> response
                   :choices
                   first
                   :message
                   :content)]
    script))

(defn audio-generate-payload [script]

  (let [clj-payload {
                        "model" "tts-1"
                        "input" script
                        "voice" "onyx"
                      }]
    (json/generate-string clj-payload)))

(defn ask-chatgpt-for-audio [script]
  (let [payload (audio-generate-payload script)
        response  (client/post "https://api.openai.com/v1/audio/speech" {:headers {"Authorization" (str "Bearer " chatgpt-api-key)
                                                                                       "Content-Type" "application/json"}
                                                                             :body payload
                                                                         :as :byte-array})]
    response))


(defn caption-generate-payload [article]

  (let [title (:title article)
        description (:description article)
        source (:name (:source article))
        content (:content article)
        clj-payload {"model" "gpt-4o-mini"
                     "messages" [{"role" "system"
                                  "content" "You are a professional social media manager working for 'The Cyber Bulletin.' Your task is to prepare a short caption for a reel or post based on specific news articles."}
                                 {"role" "user"
                                  "content" (format "Below is the information for a specific news article. Based on this:
                                                       - Write a professional and engaging caption for a social media reel or post that directly relates to this article.
                                                       - Focus on the key points of the article to intrigue viewers and encourage them to engage.
                                                       - Limit the caption to 2-3 sentences, focusing on the title and description.
                                                       - Do not include generic statements or placeholders; ensure the caption directly reflects the article’s content.
                                                       - At the end of the caption, include the hashtags: #ScienceNews #Science #TheCyberBulletin.

                                                     Title: %s
                                                     Description: %s
                                                     Source: %s
                                                     Content: %s" title description source content)}]}]
    (json/generate-string clj-payload)))

(defn ask-chatgpt-for-caption [article]
  (println "Calling ask-chatgpt-for-caption...")
  (let [payload (caption-generate-payload article)
        response-str (:body (client/post "https://api.openai.com/v1/chat/completions" {:headers {"Authorization" (str "Bearer " chatgpt-api-key)
                                                                                                 "Content-Type" "application/json"}
                                                                                       :body payload}))
        response (json/parse-string response-str true)
        caption (-> response
                    :choices
                    first
                    :message
                    :content)]

    caption))
