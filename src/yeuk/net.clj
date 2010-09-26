;; network functions

(ns yeuk.net
  (:use [yeuk db core prefs])
  (:import [java.net ServerSocket Socket]
	   [java.io ObjectInputStream ObjectOutputStream BufferedReader PrintWriter])
  (:require [clojure.java.io :as io]))

(defn send-raw [text client]
  "Sends the text as is to the socket"
  (println "SENDING" text)
  (binding [*out* (:sockout client)]
    (println text)))

(defn send-raw-global [text]
  "Sends text to each of the clients"
  (doseq [client (:users @state)]
    (send-raw text (second client))))

(defn send-line [text client]
  "Sends a line of text to a client connection"
  (send-raw (str ":" (:server-name prefs)  " " text) client))

(defn send-notice [text client]
  "Sends a notice to the client"
  (send-line (str "NOTICE " (:nick client) " :" text) client))

(defn read-irc-line [client]
  "Reads a line from client. Blocks for IO"
  (binding [*in* (:sockin client)]
    (read-line)))

(defn close-socket [client]
  (.close (:socket client)))

(defn handle-USER [string client]
  "Handles the USER command"
  (let [split (.split (.trim string) " |:")
	nick (first split)
	host (second split)
	server (nth split 3)
	real (apply str (interpose " " (rest (drop 3 split))))]
    (send-notice (str "*** Found your hostname (" host ")") client)))
    

(defn log-client [client]
  "Logs the activity of client to database"
  (.start
   (Thread.
    (fn []
      (loop []
	; TODO: make this actually log
	(when-let [line (read-irc-line client)]
	  (print (:socket client) line)
	  (recur)))))))

(defn handle-connection [connection]
  "Handles a new IRC connection"
  (println "I GOT A NEW CONNECTION" connection)
  (let [in  (io/reader connection)
	out (PrintWriter. (io/writer connection) true)
	client-map { :socket connection :hostname "unknown" :nick "*" :sockin in :sockout out }]
    
    (when (:log prefs)
      (log-client client-map))
    
    (send-notice "*** Looking up your hostname..." client-map)
    (loop [ line (.trim (read-irc-line client-map))]
      (if (= (apply str (take 4 line)) "USER")
	(future (handle-USER (apply str (drop 4 line)) client-map))
	(recur (.trim (read-irc-line client-map)))))))

(defn start-server []
  "Starts up the IRC server"
  (dosync
   (alter state
	  assoc :server
	  (if (pos? (:server-max-conn prefs))
	    (ServerSocket. (:server-port prefs) (:server-max-conn prefs))
	    (ServerSocket. (:server-port prefs))))))

  (defn stop-server []
    "Stops the IRC server. Users should be disconnected already"
    (.close (:server @state)))

  (defn server-listen []
    "Accepts connections to the server, infinite loop in a new thread"
    (.start
     (Thread.
      (fn []
	(while true
	  (let [connection (.accept (:server @state))]
	    ; using future so that handle-connection doesn't block
	    (future (handle-connection connection))))))))
	 