(ns yeuk.replies)

(def replies-numeric
     {
      :ERR_NOSUCHNICK 401
      :ERR_NOSUCHSERVER 402
      :ERR_NOSUCHCHANNEL 403
      :ERR_CANNOTSENDTOCHAN 404
      :ERR_TOOMANYCHANNELS 405
      :ERR_WASNOSUCHNICK 406
      :ERR_TOOMANYTARGETS 407
      :ERR_NOORIGIN 409
      
      :ERR_NORECIPIENT 411
      :ERR_NOTEXTTOSEND 412
      :ERR_NOTOPLEVEL 413
      :ERR_WILDTOPLEVEL 414

      :ERR_UNKNOWNCOMMAND 421
      :ERR_NOMOTD 422
      :ERR_NOADMININFO 423
      :ERR_FILEERROR 424

      :ERR_NONICKNAMEGIVEN 431
      :ERR_ERRONEUSNICKNAME 432
      :ERR_NICKNAMEINUSE 433
      :ERR_NICKCOLLISION 436

      :ERR_USERNOTINCHANNEL 441
      :ERR_NOTONCHANNEL 442
      :ERR_USERONCHANNEL 443
      :ERR_NOLOGIN 444
      :ERR_SUMMONDISABLED 445
      :ERR_USERSDISABLE 446

      :ERR_NOTREGISTERED 451
      
      :ERR_NEEDMOREPARAMS 461
      :ERR_ALREADYREGISTERED 462
      :ERR_NOPERMFORHOST 463

      :ERR_PASSWORDMISMATCH 464
      :ERR_YOUREBANNEDCREEP 465

      :ERR_KEYSET 467
      :ERR_CHANNELISFULL 471
      :ERR_UNKNOWNMODE 472
      :ERR_INVITEONLYCHAN 473
      :ERR_BANNEDFROMCHAN 474
      :ERR_BADCHANNELKEY 475
      :ERR_NOPRIVILEGES 481
      :ERR_CHANOPRIVSNEEDED 482
      :ERR_CANTKILLSERVER 483

      :ERR_NOOPERHOST 491
      :ERR_UMODEUNKNOWNFLAG 501
      :ERR_USERSDONTMATCH 502
      })

(def replies-text
     {
      401 "%NICK% :No such nick/channel"
      402 "%SERV% :No such server"
      403 "%CHAN% :No such channel"
      404 "%CHAN% :Cannot send to channel"
      405 "%CHAN% :You have joined too many channels"
      406 "%NICK% :There was no such nick"
      407 "%TARG% :Duplicate recipients. No message delivered"
      409 ":No origin specified"
      
      411 ":No recipient given"
      412 ":No text to send"
      413 "%MASK% :No toplevel domain specified"
      414 "%MASK  :Wildcard in toplevel domain"
      
      421 "%CMD% :Unknown command"
      422 ":MOTD missing"
      423 "%SERV% :No administrative info available"
      424 ":File error doing something on something"

      431 ":No nickname given"
      432 "%NICK% :Erroneus nickname"
      433 "%NICK% :Nickname is already in use"
      436 "%NICK% :Nickname collision KILL"

      441 "%NICK% %CHAN% :They aren't on that channel"
      442 "%CHAN% :You're not on that channel"
      443 "%USER% %CHAN% :is already in channel"

      444 "%USER :User not logged in"
      445 ":SUMMON has been disabled"
      446 ":USERS has been disabled"

      451 ":You have not registered"
      461 "%CMD% :Not enough parameters"
      462 ":You may not register"
      463 ":Your host isn't among the privileged"

      464 ":Password incorrect"
      465 ":You are banned from this server"
      467 "%CHAN% :Channel key already set"
      471 "%CHAN% :Cannot join channel (+l)"
      472 "%MODE% :is unknown mode char to me"
      473 "%CHAN% :Cannot join channel (+i)"
      474 "%CHAN% :Cannot join channel (+b)"
      475 "%CHAN% :Cannot join channel (+k)"
      481 ":Permission Denied - You ain't no op."
      482 "%CHAN% :You're not channel operator"
      483 ":YOU CAN'T KILL A SERVER!"

      491 ":No O-lines for your host"
      501 ":Unknown MODE flag"
      502 ":Can't change mode for other users"
      })


(defn numeric-for-error [mode]
  "Returns the matching numeric code for specific error"
  (mode replies-numeric))

(defn text-for-error [mode-or-num]
  "Returns text for the matching error code or name"\
  (if (= (class mode-or-num) clojure.lang.Keyword)
    ((numeric-for-error mode-or-num) replies-text)
    (replies-text mode-or-num)))
    