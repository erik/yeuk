;; Preferences file for server config. This goes in ~/.yeuk

{
 :server-name "test-server"
 :server-motd "Welcome to my server!"
 :server-port 6667
 :server-max-conn -1 ; maximum number of connections (-1 for unlimited)
 
 :db-name "yeuk" ; name of mongo database
 :log false ; whether to keep a text log or not
 }
 