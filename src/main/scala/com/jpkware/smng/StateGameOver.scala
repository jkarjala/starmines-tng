package com.jpkware.smng

import com.definitelyscala.phaser.{Game, State}
import org.scalajs.dom.raw.Element

class StateGameOver(game: Game, options: Map[String,String], status: Element) extends State {

  var keyDown: Boolean = _

  override def create(): Unit = {

    game.add.bitmapText(game.width/2,300, GlobalRes.FontId, "Game Over", 128).anchor.set(0.5,0.5)

    val button = PhaserButton.add(game, game.width/2-150,game.height-200, "", textFrame=PhaserButton.FrameRetry)
    button.events.onInputUp.add(gotoRetry _, null, 1)
    keyDown = PhaserKeys.isFireDown(game)

    val buttonMenu = PhaserButton.add(game, game.width/2+150,game.height-200, "", textFrame=PhaserButton.FrameGrid)
    buttonMenu.events.onInputUp.add(gotoLevels _, null, 1)

  }

  override def update(): Unit = {
    if (keyDown) keyDown = !PhaserKeys.isFireDown(game)
    if (!keyDown && PhaserKeys.isFireDown(game)) gotoRetry()
    if (game.input.keyboard.isDown(27)) gotoLevels()
  }

  def gotoMenu(): Unit = {
    game.state.start("menu", args = "gameover", clearCache = false, clearWorld = true)
  }

  def gotoLevels(): Unit = {
    game.state.start("levels", args = "gameover", clearCache = false, clearWorld = true)
  }
  def gotoRetry(): Unit = {
    game.state.start("play", args = "restore", clearCache = false, clearWorld = true)
  }
}