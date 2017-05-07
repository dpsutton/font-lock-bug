(ns font-lock-bug.core
  (:require [font-lock-bug.import :as i]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn uses-import
  [x]
  (i/bar x))
