(ns libpython-clj-talk.basics
  (:require [libpython-clj.python :as py :refer [py.. py.]]))


(defn -main
  [& _]
  (py/initialize!)

  (def panda (py/import-module "panda3d"))

  (def show-base (py/get-attr (py/import-module "direct.showbase.ShowBase") "ShowBase"))

  ;;https://docs.panda3d.org/1.10/python/introduction/tutorial/loading-the-grassy-scenery
  (def app ((py/create-class "app" [show-base]
                             {"__init__" (py/make-tuple-instance-fn
                                          (fn [self]
                                            (py. show-base __init__ self)
                                            (py/set-attr! self "scene" (py.. self -loader (loadModel "models/environment")))
                                            (py.. self -scene (reparentTo (py.. self -render)))
                                            (py.. self -scene (setScale 0.25 0.25 0.25))
                                            (py.. self -scene (setPos -8 42 0))
                                            nil))})))

  (py.. app (run)))

#_(-main)
