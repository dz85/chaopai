(ns chaopai.views.layout
  (:use noir.request)
  (:require [clabango.parser :as parser]))

(def template-path "chaopai/views/templates/")

(defn render [template & [params]]
  (parser/render-file (str template-path template)
                      (assoc params :context (:context *request*))))

