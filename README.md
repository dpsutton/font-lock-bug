# font-lock-bug

Minimum example of bug in CIDER font locking where depdencies are not
font locked.

## Usage

This is a minimum example to demonstrate and work on the cider bug at [bug 1985](https://github.com/clojure-emacs/cider/issues/1985).

### Modifications To CIDER ###

We are looking at font-locking, and if you involve the core things get pretty nasty.

In order to make sure that there's as little as possible in the mechanism, modify `cider--compile-font-lock-keywords` so that it does as little work as possible:

    (defun cider--compile-font-lock-keywords (symbols-plist core-plist)
      "Return a list of font-lock rules for the symbols in SYMBOLS-PLIST and CORE-PLIST."
      (let ((cider-font-lock-dynamically ;; (if (eq cider-font-lock-dynamically t)
                                         ;;     '(function var macro core deprecated)
             ;;   cider-font-lock-dynamically)
             '(function)
                                         )
            deprecated enlightened
            macros functions vars instrumented traced)
        (cl-labels ((handle-plist
                     (plist)
                     (let ((do-function (memq 'function cider-font-lock-dynamically))
                           (do-var (memq 'var cider-font-lock-dynamically))
                           (do-macro (memq 'macro cider-font-lock-dynamically))
                           (do-deprecated (memq 'deprecated cider-font-lock-dynamically)))
                       (while plist
                         (let ((sym (pop plist))
                               (meta (pop plist)))
                           ;; (pcase (nrepl-dict-get meta "cider.nrepl.middleware.util.instrument/breakfunction")
                           ;;   (`nil nil)
                           ;;   (`"#'cider.nrepl.middleware.debug/breakpoint-if-interesting"
                           ;;    (push sym instrumented))
                           ;;   (`"#'cider.nrepl.middleware.enlighten/light-form"
                           ;;    (push sym enlightened)))
                           ;; ;; The ::traced keywords can be inlined by MrAnderson, so
                           ;; ;; we catch that case too.
                           ;; ;; FIXME: This matches values too, not just keys.
                           ;; (when (seq-find (lambda (k) (and (stringp k)
                           ;;                                  (string-match (rx "clojure.tools.trace/traced" eos) k)))
                           ;;                 meta)
                           ;;   (push sym traced))
                           ;; (when (and do-deprecated (nrepl-dict-get meta "deprecated"))
                           ;;   (push sym deprecated))
                           (cond ((and do-macro (nrepl-dict-get meta "macro"))
                                  (push sym macros))
                                 ((and do-function (nrepl-dict-get meta "arglists"))
                                  (push sym functions))
                                 (do-var (push sym vars))))))))
          (when (memq 'core cider-font-lock-dynamically)
            (let ((cider-font-lock-dynamically '(function var macro core deprecated)))
              (handle-plist core-plist)))
          (handle-plist symbols-plist))
        `(
          ,@(when macros
              `((,(concat (rx (or "(" "#'")) ; Can't take the value of macros.
                          "\\(" (regexp-opt macros 'symbols) "\\)")
                 1 (cider--unless-local-match font-lock-keyword-face))))
          ,@(when functions
              `((,(regexp-opt functions 'symbols) 0
                 (cider--unless-local-match font-lock-function-name-face))))
          ;; ,@(when vars
          ;;     `((,(regexp-opt vars 'symbols) 0
          ;;        (cider--unless-local-match font-lock-variable-name-face))))
          ;; ,@(when deprecated
          ;;     `((,(regexp-opt deprecated 'symbols) 0
          ;;        (cider--unless-local-match 'cider-deprecated-face) append)))
          ;; ,@(when enlightened
          ;;     `((,(regexp-opt enlightened 'symbols) 0
          ;;        (cider--unless-local-match 'cider-enlightened-face) append)))
          ;; ,@(when instrumented
          ;;     `((,(regexp-opt instrumented 'symbols) 0
          ;;        (cider--unless-local-match 'cider-instrumented-face) append)))
          ;; ,@(when traced
          ;;     `((,(regexp-opt traced 'symbols) 0
          ;;        (cider--unless-local-match 'cider-traced-face) append)))
          )))


## License

Copyright © 2017 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
