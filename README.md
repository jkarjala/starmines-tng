# StarMines: The Next Generation (SM:TNG)

StarMines is a space shooter inspired by old classics like Asteroids and Omega Race.

The SM:TNG is the [StarMines for Java](http://jpkware.com/smj/StarMines.html) rebooted 
using the HTML5 technology and modern tools.

You can play the game at [smtng.jpkware.com](https://smtng.jpkware.com/).

See the info screen in the game for credits and tools used. 

## Development

- install sbt 1.x
- run "sbt ~fastOptJs"
- open http://localhost:12345/classes/index-dev.html in your browser
- whenever you edit the scala sources, they are compiled automatically and browser is refreshed
- IntelliJ Idea recommended  as the IDE

The server folder contains a PHP script for highscores. These
require a local web server with PHP and MySQL to be set up.

The client always posts high scores to jpkware.com, howeever, the server
accepts the requests only from configured host URLs.

The deploy.sh script can be used to deploy to a remote linux host,
assuming SSH keys have been set up.

## License

StarMines: The Next Generation copyright [Jari Karjala](https://www.jarikarjala.com/) 
1999-2020. Licensed under [GNU General Public License v3](LICENSE).
