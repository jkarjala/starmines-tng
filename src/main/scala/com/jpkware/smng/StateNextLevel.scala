package com.jpkware.smng

import com.definitelyscala.phaser.{Game, State}
import org.scalajs.dom.raw.Element

import scala.scalajs.js

class StateNextLevel(game: Game, options: Map[String,String], status: Element) extends State {

  var keyDown: Boolean = _
  var result: String = _

  override def init(args: js.Any*): Unit = {
    args.headOption match {
      case str: Some[js.Any] =>
        Logger.info(s"init ${str.get}")
        result = str.get.asInstanceOf[String]
    }
  }

  override def create(): Unit = {

    game.add.bitmapText(game.width/2,300, "font", result, 64).anchor.set(0.5,0.5)

    val button = PhaserButton.add(game, game.width/2,game.height-200, "Next")
    button.events.onInputUp.add(gotoPlay _, null, 1)
    keyDown = PhaserKeys.isFireDown(game)
  }

  override def update(): Unit = {
    if (keyDown) keyDown = !PhaserKeys.isFireDown(game)
    if (!keyDown && PhaserKeys.isFireDown(game)) gotoPlay()
  }

  def gotoPlay(): Unit = {
    game.state.start("play", args = "nextlevel", clearCache = false, clearWorld = true)
  }

}