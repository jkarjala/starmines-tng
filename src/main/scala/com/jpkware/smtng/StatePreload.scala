/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{BitmapText, Game, Sprite, State}
import org.scalajs.dom.raw.Element
import org.scalajs.dom

class StatePreload(game: Game, options: Map[String,String], status: Element) extends State {

  var preloadBar: Sprite = _
  var ship: Sprite = _
  var step: Int = 10
  override def preload(): Unit = {

    game.add.sprite(0,0, GlobalRes.MenuBg).scale.set(2,2)
    GlobalRes.drawLogo(game)
    GlobalRes.drawCopy(game)

    ship = game.add.sprite(game.width / 4, game.height / 2 + 100, "ship-preload")
    ship.anchor.set(0, 0.5)

    preloadBar = game.add.sprite(game.width / 4 + 20, game.height / 2 + 100, "preloaderBar")
    preloadBar.anchor.set(1, 0.5)
    preloadBar.scale.set(1.25,1.25)
    game.load.setPreloadSprite(preloadBar)

    val text = game.add.bitmapText(game.width/2,game.height/2-100, GlobalRes.FontMoonId, "", 32)
    text.anchor.set(0.5,0.5)
    game.load.onFileComplete.add((progress: Int) => {
      text.text = s"$progress%"
    }, null, 1)

    game.load.spritesheet(GlobalRes.ButtonId, "res/buttons.png", 128, 128)
    game.load.atlasJSONHash(GlobalRes.MainAtlasId, s"res/main.png", s"res/main.json")
    game.load.atlasJSONHash(GlobalRes.EnemiesAtlasId, s"res/enemies.png", s"res/enemies.json")
    Scorebox.preloadResources(game)
    Player.preloadResources(game)
    Explosion.preloadResources(game)
    Producer.preloadResources(game)
    StatePlay.preloadResources(game)
    StateNextLevel.preloadResources(game)
    (1 to StarMinesNG.maxBackground).foreach(i => game.load.image(s"space$i", s"res/space$i.jpg"))
  }

  override def update(): Unit = {
    ship.x += step
    preloadBar.x += step
    step += 2
    if (ship.x>game.width) {
      // Now that things are loaded, it is OK to pause on losing focus.
      game.stage.disableVisibilityChange = false
      game.state.start("menu", args = null, clearCache = false, clearWorld = true)
      ship.destroy(true)
      preloadBar.destroy(true)
    }
  }

}

object GlobalRes {
  val MainAtlasId = "sprites"
  val EnemiesAtlasId = "enemies"
  val FontId = "font-x"
  val FontMoonId = "font"
  val ButtonId = "button"
  val MenuBg = "space0"

  val Title = "StarMines"
  val SubTitle = "THE NEXT GENERATION"

  def drawLogo(game: Game, offset: Int = 260): Unit = {
    game.add.bitmapText(game.width/2,offset, FontMoonId, Title, 128).anchor.set(0.5,0.5)
    game.add.bitmapText(game.width/2,offset+40, FontMoonId, SubTitle, 32).anchor.set(0.5,0.5)
  }

  def drawCopy(game: Game): Unit = {
    val copy: BitmapText = game.add.bitmapText(game.width / 2, game.height - 32, GlobalRes.FontId, "Copyright 2018-2019 Jari Karjala - www.jpkware.com", 24)
    copy.anchor.set(0.5, 0.5)
    Progress.fetchBuild(res => copy.setText(res + " - " + copy.text))
  }
}