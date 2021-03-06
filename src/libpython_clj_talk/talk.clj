;; https://github.com/clj-python/libpython-clj (Chris Nuernberger)
;; https://github.com/tristanstraub/clj-melb-libpython/

(ns libpython-clj-talk.talk
  (:require [clojure.java.io :as io]
            [libpython-clj.python :as py]))

;; starting

(require '[libpython-clj.python :as py :refer [py..]])

(py/initialize!)

(def sys (py/import-module "sys"))

(py.. sys -version)

;; two way dict

(-> (py/->python {:a 1})
    (py/get-item "a")
    (py/as-jvm))

(def data (py/->python {:a 1}))

(-> (py/as-jvm data)
    (get "a"))

(py/set-item! data "b" 2)

(-> (py/as-jvm data)
    (get "b"))

(py/as-jvm (py/get-item data "b"))

;; mixed with python code

(require '[libpython-clj.metadata :as pymeta])

(py/dir sys)
(print (pymeta/doc sys))
(print (pymeta/doc (py.. sys -argv)))
(py/get-attr sys "path")

;; src/talk.py
(py.. sys -path (append "src"))

(def talk (py/import-module "talk"))

(py.. talk (inc 1))
(py.. talk (apply println 1 2 3 :a 4))
(py.. talk (apply (fn [{:keys [a]}]
                    (inc a))
                  :a 4))

(py.. talk (get_global_x))

(py/set-attr! talk "x" 2)

(py.. talk (get_global_x))

;; reload python code
(py.. (py/import-module "importlib") (reload talk))

;; escape hatch

(-> (py/run-simple-string "
x = 1 + 2
y = 3 + x")
    (get-in [:globals :y]))

;; spacy -- https://github.com/explosion/spaCy

(def spacy (py/import-module "spacy"))
(def nlp (py.. (py/import-module "en_core_web_sm")
               (load)))

(def doc (nlp "Autonomous cars shift insurance liability toward manufacturers"))

(->> doc
     (map (fn [word]
            [(py.. word -text)
             (seq (py.. word -children))]))
     (filter (complement (comp nil? second))))

(def displacy (py/get-attr spacy "displacy"))
(spit "displaced.svg" (py.. displacy (render doc :style "dep")))

;; numpy

(require '[libpython-clj.require :refer [require-python]])

(require-python 'cv2 'numpy)

;; run first from shell: convert displaced.svg displaced.png
(py.. (cv2/imread "displaced.png") -shape)

(-> (cv2/imread "displaced.png")
    (numpy/transpose [1 0 2])
    (->> (cv2/imwrite "transposed.png")))

;; see panda3d.clj

;; see github.com/tristanstraub/blender-clj-addon
