;; TODO: this file probably isn't needed, as congomongo isn't all that verbose in the first place

(ns yeuk.db
  (:use yeuk.prefs)
  (:require [somnium.congomongo :as mongo]))


(defn db-init []
  "Initializes mongodb"
  (mongo/mongo! :db (:db-name prefs)))

(defn db-add [table map]
  "Adds map to the database table named by 'table'"
  (mongo/insert! table map))


