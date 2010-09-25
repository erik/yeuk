(ns yeuk.crypt
  (:import [java.security MessageDigest NoSuchAlgorithmException]
	   [sun.misc BASE64Encoder CharacterEncoder]))

(defn encrypt [text]
  "Encrypts text using SHA-1 algorithm"
  (let [md (MessageDigest/getInstance "SHA-1")]
    (.update md (.getBytes text "UTF-8"))
    (.encode (BASE64Encoder.) (.digest md))))
    