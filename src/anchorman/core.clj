(ns anchorman.core
  (:require [anchorman.scripts :as scripts])
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Starting The Cyber Bulletin process...")
  (scripts/generate-audio)
  (scripts/generate-caption)
  (println "Process complete."))
