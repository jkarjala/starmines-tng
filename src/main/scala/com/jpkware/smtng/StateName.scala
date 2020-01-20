/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2020 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{Game, State}
import org.scalajs.dom.raw.Element

class StateName(game: Game, options: Map[String,String]) extends State {

  var keyboard: TouchKeyboard = _

  override def create(): Unit = {

    game.add.sprite(0,0, GlobalRes.MenuBg).scale.set(2,2)
    PhaserButton.addMinMax(game)

    val button = PhaserButton.addExit(game, game.width - 40, 40, scale = 0.5)
    button.events.onInputUp.add(() => gotoMenu(), null, 1)

    if (Progress.state.maxLevel>1)
      game.add.bitmapText(300,100, GlobalRes.FontId, "Warning: changing your name will reset game progress!", 48)
    else
      game.add.bitmapText(300,950, GlobalRes.FontId, "You can choose a new player name if you start the game from the beginning", 32)

    game.add.bitmapText(300,200, GlobalRes.FontId, "Please Input Your Player Name:", 32)

    keyboard = new TouchKeyboard(game, 360,300, 15, Progress.getName,
      (name: String) => {
        if (Progress.getName!=name) {
          Progress.resetProgress()
          Progress.saveName(name)
        }
        game.state.start("play", args = "start", clearCache = false, clearWorld = false)
      }
    )

    game.add.bitmapText(300,900, GlobalRes.FontId, "Your Player Name will be shown in the high score lists and stored in the server", 32)
  }

  override def update(): Unit = {
    if (game.input.keyboard.isDown(27)) gotoMenu()
  }

  def gotoMenu(): Unit = {
    game.state.start("menu", args = "name", clearCache = false, clearWorld = true)
  }
}