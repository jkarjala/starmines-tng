/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{Game, Sprite, State}
import org.scalajs.dom.raw.Element

class StateMenu(game: Game, options: Map[String,String]) extends State {

  var preloadBar: Sprite = _

  override def preload(): Unit = {
  }

  override def create(): Unit = {
    game.add.sprite(0,0, GlobalRes.MenuBg).scale.set(2,2)

    val help = if (game.device.desktop || options.contains("touch")) "Control your ship with arrow keys and space, or z,x,n,m, or mouse"
    else "Use the touch buttons to control your ship"

    game.add.bitmapText(game.width/2,game.height/2-220, GlobalRes.FontId, "StarMines", 128).anchor.set(0.5,0.5)
    game.add.bitmapText(game.width/2,game.height/2-180, GlobalRes.FontId, "THE NEXT GENERATION", 32).anchor.set(0.5,0.5)

    game.add.bitmapText(game.width/2,game.height-200, GlobalRes.FontId,
      "Collect all Bonusoids for maximum score and ship upgrades", 32).anchor.set(0.5,0.5)
    game.add.bitmapText(game.width/2,game.height-150, GlobalRes.FontId, help, 32).anchor.set(0.5,0.5)

    game.add.bitmapText(game.width/2,game.height-50, GlobalRes.FontId, "Copyright 2018 Jari.Karjala@iki.fi", 32).anchor.set(0.5,0.5)

    val (b1x,b2x,b3x) = if (Progress.hasCheckpoint) (-200,0,200) else (-100, 0, 100)
    val buttonY = game.height/2

    val button = PhaserButton.add(game, game.width/2+b1x, buttonY, "", scale = 1.0, textFrame=PhaserButton.FramePlay)
    button.events.onInputUp.add(startGame _, null, 1)

    if (Progress.hasCheckpoint) PhaserButton.addRetry(game, game.width/2+b2x, buttonY)

    PhaserButton.addLevels(game, game.width/2+b3x, buttonY)

    PhaserButton.addMinMax(game)
  }

  override def update(): Unit = {
    if (PhaserKeys.isFireDown(game)) startGame()
  }

  def startGame(): Unit = {
    Progress.resetCheckpoint()
    game.state.start("play", args = "start", clearCache = false, clearWorld = true)
  }
}