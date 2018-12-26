/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{Game, Sprite, State}
import org.scalajs.dom.raw.Element

import scala.scalajs.js

class StatePreload(game: Game, options: Map[String,String], status: Element) extends State {

  var preloadBar: Sprite = _
  var ship: Sprite = _
  var step: Int = 10
  override def preload(): Unit = {

    game.add.sprite(0,0, GlobalRes.MenuBg).scale.set(2,2)

    ship = game.add.sprite(game.width / 4, game.height / 2, "ship-preload")
    ship.anchor.set(0, 0.5)

    preloadBar = game.add.sprite(game.width / 4 + 20, game.height / 2, "preloaderBar")
    preloadBar.anchor.set(1, 0.5)
    preloadBar.scale.set(1.25,1.25)
    game.load.setPreloadSprite(preloadBar)

    game.load.onFileComplete.add((progress: Int) => {
      status.innerHTML  = "Loading resources " + progress.toString + "%"
    }, null, 1)

    game.load.bitmapFont(GlobalRes.FontId, "res/font.png", "res/font.fnt")
    // game.load.spritesheet(GlobalRes.ButtonId, "res/button.png", 128, 128)
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
      if (Progress.hasCheckpoint)
        game.state.start("play", args = "restore", clearCache = false, clearWorld = false)
      else {
        if (Progress.state.name.get.isEmpty)
          game.state.start("name", args = null, clearCache = false, clearWorld = false)
        else
          game.state.start("menu", args = null, clearCache = false, clearWorld = false)
      }
      ship.destroy(true)
      preloadBar.destroy(true)
      status.innerHTML = ""
    }
  }

}

object GlobalRes {
  val MainAtlasId = "sprites"
  val EnemiesAtlasId = "enemies"
  val FontId = "font"
  val ButtonId = "button"
  val MenuBg = "space0"
}