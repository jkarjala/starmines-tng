/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2020 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{Game, _}

import scala.scalajs.js


class StateBoot(game: Game, options: Map[String,String]) extends State {

  override def init(args: js.Any*): Unit = {
    game.clearBeforeRender = false
    game.stage.disableVisibilityChange = true
    game.time.advancedTiming = true
    game.scale.pageAlignHorizontally = true
    game.scale.pageAlignVertically = true
    game.scale.windowConstraints = js.Dynamic.literal(right="visual", bottom="visual")
    game.scale.fullScreenScaleMode = ScaleManager.SHOW_ALL
    game.scale.scaleMode = ScaleManager.SHOW_ALL
    game.input.keyboard.addKeyCapture(32)
  }

  override def preload(): Unit = {
    game.load.image("preloaderBar", "res/flame.png")
    game.load.image("ship-preload", "res/ship-preload.png")
    game.load.image(GlobalRes.MenuBg, "res/space0.jpg")
    game.load.bitmapFont(GlobalRes.FontMoonId, "res/font.png", "res/font.fnt")
    game.load.bitmapFont(GlobalRes.FontId, "res/font-x.png", "res/font-x.fnt")
  }

  override def create(): Unit = {
    game.input.addPointer() // 3rd touch
    game.input.addPointer() // 4th touch
    game.canvas.oncontextmenu = (e) => { e.preventDefault() }
    game.state.start("preloader", args = js.Array[String](), clearCache = false, clearWorld = false)
  }
}