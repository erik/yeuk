;; network functions

(ns yeuk.net
  (:use [yeuk db core prefs replies])
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

(defn send-numeric [num string client]
  "Sends a message with a numeric code to the client"
  (println "SEND-NUMERIC")
  (send-line (str (format "%03d" num) " " string) client))

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

(defn handle-NICK [string client]
  "Handles the NICK command"
  (let [nick (.toLowerCase (.trim string))]
    (println (str "NICK = >>"nick"<<")
    (cond
     (not (seq nick)) (do (println "OMG YOU SUCKS")
			   (send-numeric
		    (numeric-for-error :ERR_NONICKNAMEGIVEN)
		    (text-for-error :ERR_NONICKNAMEGIVEN)
		    client))
     (= nick (:nick client)) nil
     (:users nick) 123))))

(defn handle-defaults [client]
  "Handles the default commands from connection"
  (loop [ line (read-irc-line client) user? false nick? false]
    (println "OHAI" line)
    (cond
     (= (apply str (take 4 line)) "USER") (do
					    (println "Going to call handle-USER")
					    (future (handle-USER (apply str (drop 4 line)) client))
					    (when-not nick? (recur (read-irc-line client) true nick?)))
     (= (apply str (take 4 line)) "NICK") (do
					    (println "GOING TO CALL HANDLE NICK")
					    (future (handle-NICK (apply str (drop 4 line)) client))
					    (when-not user? (recur (read-irc-line client) user? true)))
     :else (do
	     (send-notice (str "I don't know what to do with this: \"" line "\"") client)
	     (when-not (and user? nick?) (recur (read-irc-line client) user? nick?))))))


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
    (handle-defaults client-map)))

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
	 