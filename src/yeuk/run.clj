(ns yeuk.run
  (:use [yeuk net core db prefs]))

(db-init)
(start-server)
(server-listen)