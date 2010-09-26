;; TODO: this file probably isn't needed, as congomongo isn't all that verbose in the first place

(ns yeuk.db
  (:use [yeuk prefs crypt])
  (:require [somnium.congomongo :as mongo]))


(defn db-init []
  "Initializes mongodb"
  (mongo/mongo! :db (:db-name prefs)))

(defn db-add [table map]
  "Adds map to the database table named by 'table'"
  (mongo/insert! table map))

(defn db-remove [table map]
  "Removes from table the value pointed to by map"
  (mongo/destroy! table map))

(defn db-add-user [nick password privs]
  "Adds a user into the database. (password is plaintext)"
  (mongo/insert! :users { :nick nick :pass (encrypt password) :privs privs }))

(defn db-remove-user [nick]
  "Removes a user from the database"
  (mongo/destroy! :users { :nick nick }))

