(ns yeuk.core)


; global server state, info, various stuff
(def state (ref {:server-start (System/currentTimeMillis)
		 :users {} }))

