# GameJolt API [![Build Status](https://travis-ci.org/born2snipe/gamejolt-api.svg)](https://travis-ci.org/born2snipe/gamejolt-api)

== Current Features
- access to available trophies (earned/unearned)
- access to achieve a trophy
- access to highscores
- access to achieve user highscores
- supports gzip & deflate compression
- supports custom serialization of user/game data
- access to save/read/remove data for users or your game
- trophy achievement management

== Example Usage

    int gameId = 1111;
    String privateKey = "your personal privatekey given to you by game jolt";
    String playerUsername = "player";
    String playerHash = "playerHash";

    GameJolt gj = new GameJolt(gameId, privateKey);
    if (gj.verifyUser(playerUsername, playerHash))) {
        List<Trophy> trophies = gj.getAllTrophies();
    }

== Coming soon...
- quickplay support
- guest player highscores
