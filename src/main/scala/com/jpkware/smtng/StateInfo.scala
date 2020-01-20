/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2020 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{Game, State}

class StateInfo(game: Game, options: Map[String,String]) extends State {

  val credits: String ="""
      |- Phaser CE 2D game toolkit
      |- Scala.js Scala to JavaScript compiler
      |- IntelliJ Idea Community Edition IDE
      |- TexturePacker sprite atlas builder
      |- Persistence of Vision 3D ray-tracer
      |- miniPaint online bitmap editor
      |- Littera online bitmap font generator
      |- Base font www.dafont.com/xolonium.font
      |- Logo font www.dafont.com/moonhouse.font
      |- Space photographs from hubblesite.org
      |- Button base shape from opengameart.org
      |- Most Icons from useiconic.com/open
      |- Some Icons by Freepik from www.flaticon.com
      |- Some Sound FX from freesound.org
      |- Some Sound FX from www.freesfx.co.uk
      |- Music by Eric Matyas www.soundimage.org
      |- Audacity Free audio editor
      """.stripMargin

  override def create(): Unit = {

    game.add.sprite(0,0, GlobalRes.MenuBg).scale.set(2,2)
    PhaserButton.addMinMax(game)

    GlobalRes.drawLogo(game, 100)
    GlobalRes.drawCopy(game)

    val header = game.add.bitmapText(game.width/2,200, GlobalRes.FontId, "Tools and Credits", 40)
    header.anchor.set(0.5,0)
    val text = game.add.bitmapText(game.width/2,230, GlobalRes.FontId, credits.replace("\n",""), 32)
    text.anchor.set(0.5,0)

    val button = PhaserButton.addExit(game, game.width - 64, 64, scale = 0.6)
    button.events.onInputUp.add(() => gotoMenu(), null, 1)
  }

  def gotoMenu(): Unit = {
    game.state.start("menu", args = "info", clearCache = false, clearWorld = true)
  }
}