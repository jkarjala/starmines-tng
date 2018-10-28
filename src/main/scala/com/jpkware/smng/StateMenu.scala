package com.jpkware.smng

import com.definitelyscala.phaser.{Game, Sprite, State}
import org.scalajs.dom.raw.Element

class StateMenu(game: Game, options: Map[String,String], status: Element) extends State {

  var preloadBar: Sprite = _

  override def preload(): Unit = {
  }

  override def create(): Unit = {
    status.innerHTML = ""
    val help = if (game.device.desktop || options.contains("touch")) "Control your ship with arrow keys and space, or z,x,n,m"
    else "Use the touch buttons to control your ship"

    game.add.bitmapText(game.width/2,game.height/2-180, "font", "StarMines", 128).anchor.set(0.5,0.5)
    game.add.bitmapText(game.width/2,game.height/2-140, "font", "THE NEXT GENERATION", 32).anchor.set(0.5,0.5)

    game.add.bitmapText(game.width/2,game.height-200, "font", "Collect all Bonusoids for maximum score", 32).anchor.set(0.5,0.5)
    game.add.bitmapText(game.width/2,game.height-150, "font", help, 32).anchor.set(0.5,0.5)

    game.add.bitmapText(game.width/2,game.height-50, "font", "Copyright 2018 Jari.Karjala@iki.fi", 32).anchor.set(0.5,0.5)

    val button = PhaserButton.add(game, game.width/2,game.height/2+40, "Play")
    button.events.onInputUp.add(startGame _, null, 1)

    if (!game.device.iOS) {
      val button2 = PhaserButton.add(game, 80, 80, "Full\nScreen\nToggle", scale = 1)
      button2.events.onInputUp.add(() => if (game.scale.isFullScreen) {
        game.scale.stopFullScreen()
      } else {
        game.scale.startFullScreen()
      }, null, 1)
    }
  }

  override def update(): Unit = {
    if (PhaserKeys.isFireDown(game)) startGame()
  }

  def startGame(): Unit = {
    game.state.start("play", args = "start", clearCache = false, clearWorld = true)
  }
}