/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
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

    game.add.bitmapText(300,200, GlobalRes.FontId, "Please Input Your Player Name:", 48)

    keyboard = new TouchKeyboard(game, 360,300, 15,
      (name: String) => {
        Progress.saveName(name)
        game.state.start("menu", args = null, clearCache = false, clearWorld = false)
      }
    )
  }

  override def update(): Unit = {

  }
}