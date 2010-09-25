(ns yeuk.prefs
  (:import [java.io File]))

(def yeuk-dir (File. (str (System/getProperty "user.home") "/.yeuk")))
(def prefs-file (str yeuk-dir "/prefs.clj"))

(def prefs (read-string (slurp prefs-file)))
