# StarMines: The Next Generation (SM:TNG)

StarMines is a space shooter inspired by old classics like Asteroids and Omega Race.

The SM:TNG is the [StarMines for Java](http://jpkware.com/smj/StarMines.html) rebooted 
using the HTML5 technology and modern tools.

You can play the game at [smtng.jpkware.com](https://smtng.jpkware.com/).

## Development

- install SBT 1.x
- run "sbt ~fastOptJS"
- open http://127.0.0.1:12345/classes/index-dev.html in your browser
- whenever you edit the Scala sources, build runs automatically and 
the browser page is refreshed
- IntelliJ Idea recommended as the IDE

The server folder contains a PHP script for high scores. Debugging
it locally requires a local web server with PHP and MySQL to be set up.

The client always posts high scores to jpkware.com, however, the server
accepts the requests only from these configured web page URLs: .jpkware.com, 
127.0.0.1:12345 and 127.0.0.1:8080.

The deploy.sh script can be used to deploy the application to a
remote Apache based host (using ~/public_html), assuming SSH keys 
have been set up. The promote.sh is used to promote a deployed 
version to production.

## License

StarMines: The Next Generation copyright [Jari Karjala](https://www.jarikarjala.com/) 
1999-2020. 

StarMines: The Next Generation is licensed under [GNU General Public License v3](LICENSE).

Many tools and resources were used to create the game, 
see [the info screen](src/main/scala/com/jpkware/smtng/StateInfo.scala) 
in the game for all the credits. 
