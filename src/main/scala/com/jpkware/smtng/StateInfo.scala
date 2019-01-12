/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{Game, State}

class StateInfo(game: Game, options: Map[String,String]) extends State {

  val credits: String =
    """
      |- Phaser CE: 2D game toolkit (https://github.com/photonstorm/phaser-ce)
      |- Scala.js: Scala compiler for JavaScript (http://www.scala-js.org/)
      |- IntelliJ Idea Community Edition: IDE for Scala (https://www.jetbrains.com/idea/)
      |- miniPaint: Online bitmap editor (https://viliusle.github.io/miniPaint/)
      |- TexturePacker: Sprite packer (https://www.codeandweb.com/texturepacker)
      |- Persistence of Vision: 3D ray-tracer (http://www.povray.org/)
      |- OpenIconic: Source for most icons (https://useiconic.com/open)
      |- OpenGameArt: Button base shape (https://opengameart.org/)
      |- Xolonium: Base font (https://www.dafont.com/xolonium.font)
      |- Moonhouse: Logo font (https://www.dafont.com/moonhouse.font)
      |- Littera: Bitmap font generator (http://kvazars.com/littera/)
      |- Freesound: Some sound effects (http://freesound.org/)
      |- Hubblesite: Space photographs (http://hubblesite.org/)
      |- Some Icons made by Freepik from www.flaticon.com
      |- Some Sound FX from www.freesfx.co.uk
    """.stripMargin

  override def create(): Unit = {

    game.add.sprite(0,0, GlobalRes.MenuBg).scale.set(2,2)
    PhaserButton.addMinMax(game)

    GlobalRes.drawLogo(game, 100)
    GlobalRes.drawCopy(game)

    val header = game.add.bitmapText(200,200, GlobalRes.FontId, "Credits:", 48)
    header.anchor.set(0,0)
    val text = game.add.bitmapText(250,250, GlobalRes.FontId, credits.replace("\n",""), 32)
    text.anchor.set(0,0)

    val button = PhaserButton.addExit(game, game.width - 40, 40, scale = 0.5)
    button.events.onInputUp.add(() => gotoMenu(), null, 1)
  }

  def gotoMenu(): Unit = {
    game.state.start("menu", args = "info", clearCache = false, clearWorld = true)
  }

  override def update(): Unit = {

  }
}