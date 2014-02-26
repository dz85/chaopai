(ns chaopai.models.db
  (:use korma.core
        [korma.db :only (defdb)])
  (:require [chaopai.models.schema :as schema]))

(defdb db schema/db-spec)
(defentity kv)
(defentity users)
(defentity product)
(defentity productpic)
(defn create-user [user]
  (insert users
          (values user)))

(defn get-user [id]
  (first (select users
                 (where {:id id})
                 (limit 1))))
