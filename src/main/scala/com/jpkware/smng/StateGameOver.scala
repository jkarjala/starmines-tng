package com.jpkware.smng

import com.definitelyscala.phaser.{Game, Sprite, State}
import org.scalajs.dom.raw.{Element, KeyboardEvent}

import scala.scalajs.js

class StateGameOver(game: Game, options: Map[String,String], status: Element) {

  val state: State = PhaserState(preload, create, update)

  var preloadBar: Sprite = _
  var keyDown: Boolean = _

  def preload(): Unit = {
  }

  def create(): Unit = {

    game.add.bitmapText(game.width/2,300, "font", "Game Over", 128).anchor.set(0.5,0.5)

    val button = PhaserButton.add(game, game.width/2,game.height-200, "Menu")
    button.events.onInputUp.add(toMenu _, null, 1)
    keyDown = PhaserKeys.isFireDown(game)
  }

  def update(): Unit = {
    if (keyDown) keyDown = !PhaserKeys.isFireDown(game)
    if (!keyDown && PhaserKeys.isFireDown(game)) toMenu()
  }

  def toMenu(): Unit = {
    game.state.start("menu", args = js.Array[String]("gameover"), clearCache = false, clearWorld = true)
  }
}