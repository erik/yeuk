;; network functions

(ns yeuk.net
  (:use [yeuk db core prefs])
  (:import [java.net ServerSocket Socket]
	   [java.io ObjectInputStream ObjectOutputStream]))

(defn send-raw [text socket]
  "Sends the text as is to the socket"
  (let [out (ObjectOutputStream. (.getOutputStream socket))]
    (.writeObject out text)))

(defn send-raw-to-all [text]
  "Sends text to each of the clients"
  (doseq [client (:users @state)]
    (send-raw text client)))

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

  ;ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
  ;String message = (String) ois.readObject();
 ; System.out.println("Message Received: " + message);
 

  (defn handle-connection [connection]
    "Handles a new IRC connection"
    (println "I GOTS A CONNECTION")
    (let [in (ObjectInputStream. (.getInputStream connection))
	  out (ObjectOutputStream. (.getOutputStream connection))]
	(send-raw "HI!\r\n" connection)))
  
  (defn server-listen []
    "Accepts connections to the server, infinite loop in a new thread"
    (.start
     (Thread.
      (fn []
	(while true
	  (let [connection (.accept (:server @state))]
	    (println "I ACCEPTED A CONNECTION!")
	    ; using future so that handle-connection doesn't block
	    (future (handle-connection connection))))))))
	 