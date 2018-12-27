/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{Game, State}
import org.scalajs.dom.raw.Element

class StateGameOver(game: Game, options: Map[String,String]) extends State {

  var keyDown: Boolean = _

  override def create(): Unit = {

    game.add.bitmapText(game.width/2,300, GlobalRes.FontId, "Game Over", 128).anchor.set(0.5,0.5)

    PhaserButton.addRetry(game, game.width/2-200,game.height-200)
    keyDown = PhaserKeys.isFireDown(game)
    PhaserButton.addLevels(game, game.width/2,game.height-200)
    PhaserButton.addExit(game, game.width/2+200,game.height-200)
  }

  override def update(): Unit = {
    if (keyDown) keyDown = !PhaserKeys.isFireDown(game)
    if (!keyDown && PhaserKeys.isFireDown(game)) PhaserButton.gotoRetry(game)
    if (game.input.keyboard.isDown(27)) PhaserButton.gotoLevels(game)
  }
}