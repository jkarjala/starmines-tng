package com.jpkware.smng

import com.definitelyscala.phaser.{Game, Sprite, State}
import org.scalajs.dom.raw.Element

import scala.scalajs.js

class StateMenu(game: Game, options: Map[String,String], status: Element) {

  val state: State = PhaserState(preload, create, update)

  var preloadBar: Sprite = _

  def preload(): Unit = {
  }

  def create(): Unit = {
    status.innerHTML = ""
    val help = if (game.device.desktop) "Use arrow keys and space, or z,x,n,m for controls" else "Use touch buttons for controls"

    game.add.bitmapText(game.width/2,game.height/2-180, "font", "StarMines", 128).anchor.set(0.5,0.5)
    game.add.bitmapText(game.width/2,game.height/2-140, "font", "THE NEXT GENERATION", 32).anchor.set(0.5,0.5)

    game.add.bitmapText(game.width/2,game.height-80, "font", help, 32).anchor.set(0.5,0.5)

    val button = PhaserButton.add(game, game.width/2,game.height/2+40, "Play")
    button.events.onInputUp.add(startGame _, null, 1)
  }

  def update(): Unit = {
    if (PhaserKeys.isFireDown(game)) startGame()
  }

  def startGame(): Unit = {
    game.state.start("play", args = js.Array[String]("start"), clearCache = false, clearWorld = true)
  }
}