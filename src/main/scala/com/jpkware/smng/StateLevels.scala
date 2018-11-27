package com.jpkware.smng

import com.definitelyscala.phaser.{Game, Pointer, Sprite, State}
import com.definitelyscala.phaserpixi.Event
import org.scalajs.dom.raw.Element

class StateLevels(game: Game, options: Map[String,String]) extends State {

  lazy val gridW = game.width/4
  lazy val gridH = game.height/4

  var currentStartLevel = 1

  override def create(): Unit = {
    createGrid(Progress.state.maxLevel/64*64 + 1, Progress.state.maxLevel)
  }

  override def update(): Unit = {
    // XXX add keyboard controls?
    if (game.input.keyboard.isDown(27)) gotoMenu()
  }

  def createGrid(startLevel: Int, maxLevel: Int): Unit = {

    var level = startLevel
    currentStartLevel = startLevel
    game.world.removeAll(destroy = true)

    for (y <- 0 to 3; x <- 0 to 3) {
        val bg = StarMinesNG.addBackground(game, level, x * gridW, y * gridH)
        bg.scale.set(gridW / bg.width, gridH / bg.height)
        if (level > maxLevel) {
          bg.alpha = 0.25
        }
        val deltaX = gridW / 5
        val marginX = x * gridW + deltaX
        val marginY = y * gridH + gridH / 2
        for (bx <- 0 to 3) {
          if (level <= maxLevel) {
            val b = PhaserButton.add(game, marginX + deltaX * bx, marginY, level.toString, scale = 0.7)
            b.events.onInputUp.add( (o: Any, p: Pointer, isOver: Boolean, lvl: Int) => startGame(lvl), null, 1, args=level)
          }
          else {
            val t = game.add.bitmapText(marginX + deltaX * bx, marginY, GlobalRes.FontId, level.toString, 32 * 0.7)
            t.alpha = 0.25
            t.anchor.set(0.5,0.5)
          }
          level += 1
        }
    }

    if (currentStartLevel > 64) {
      val button = PhaserButton.add(game, 40, game.height / 2, "", textFrame = PhaserButton.FrameLeft, scale = 0.5)
      button.events.onInputUp.add(() => {
        createGrid(currentStartLevel - 64, maxLevel)
      }, null, 1)
    }

    if (currentStartLevel + 64 < maxLevel) {
      val button = PhaserButton.add(game, game.width - 40, game.height / 2, "", textFrame = PhaserButton.FrameRight, scale = 0.5)
      button.events.onInputUp.add(() => {
        createGrid(currentStartLevel + 64, maxLevel)
      }, null, 1)
    }
    else {
      val s = game.add.sprite(game.width - 64, game.height / 2, GlobalRes.ButtonId, PhaserButton.FrameRight)
      s.scale.set(0.5,0.5)
      s.anchor.set(0.5,0.5)
      s.alpha = 0.25
    }

    val button = PhaserButton.add(game, game.width - 40, 40, "", textFrame = PhaserButton.FrameExit, scale = 0.5)
    button.events.onInputUp.add(() => gotoMenu(), null, 1)
  }

  def startGame(level: Int): Unit = {
    game.state.start("play", args = level.toString, clearCache = false, clearWorld = true)
  }

  def gotoMenu(): Unit = {
    game.state.start("menu", args = "levels", clearCache = false, clearWorld = true)
  }
}