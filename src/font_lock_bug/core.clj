(ns font-lock-bug.core
  (:require [font-lock-bug.import :as i]))

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn uses-import
  [x]
  (i/bar 2))

;; describe-var:
;; cider--dynamic-font-lock-keywords is a variable defined in ‘cider-mode.el’.
;; Its value is
;; (("\\_<\\(foo\\|i/bar\\|uses-import\\)\\_>" 0
;;   (cider--unless-local-match font-lock-function-name-face)))
;; Local in buffer font_lock_bug/core.clj; global value is nil
