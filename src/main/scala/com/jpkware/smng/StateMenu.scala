package com.jpkware.smng

import com.definitelyscala.phaser.{Game, Sprite, State}
import org.scalajs.dom.raw.Element

class StateMenu(game: Game, options: Map[String,String], status: Element) extends State {

  var preloadBar: Sprite = _

  override def preload(): Unit = {
  }

  override def create(): Unit = {
    status.innerHTML = ""
    game.add.sprite(0,0, GlobalRes.MenuBg).scale.set(2,2)

    val help = if (game.device.desktop || options.contains("touch")) "Control your ship with arrow keys and space, or z,x,n,m, or mouse"
    else "Use the touch buttons to control your ship"

    game.add.bitmapText(game.width/2,game.height/2-220, GlobalRes.FontId, "StarMines", 128).anchor.set(0.5,0.5)
    game.add.bitmapText(game.width/2,game.height/2-180, GlobalRes.FontId, "THE NEXT GENERATION", 32).anchor.set(0.5,0.5)

    game.add.bitmapText(game.width/2,game.height-200, GlobalRes.FontId,
      "Collect all Bonusoids for maximum score and permanent ship upgrades", 32).anchor.set(0.5,0.5)
    game.add.bitmapText(game.width/2,game.height-150, GlobalRes.FontId, help, 32).anchor.set(0.5,0.5)

    game.add.bitmapText(game.width/2,game.height-50, GlobalRes.FontId, "Copyright 2018 Jari.Karjala@iki.fi", 32).anchor.set(0.5,0.5)

    val button = PhaserButton.add(game, game.width/2-150,game.height/2+40, "", textFrame=PhaserButton.FramePlay)
    button.events.onInputUp.add(startGame _, null, 1)

    val button2 = PhaserButton.add(game, game.width/2+150,game.height/2+40, "", textFrame=PhaserButton.FrameGrid)
    button2.events.onInputUp.add(startLevels _, null, 1)

    PhaserButton.addMinMax(game)
  }

  override def update(): Unit = {
    if (PhaserKeys.isFireDown(game)) startGame()
  }

  def startGame(): Unit = {
    game.state.start("play", args = "start", clearCache = false, clearWorld = true)
  }

  def startLevels(): Unit = {
    game.state.start("levels", args = "start", clearCache = false, clearWorld = true)
  }

}