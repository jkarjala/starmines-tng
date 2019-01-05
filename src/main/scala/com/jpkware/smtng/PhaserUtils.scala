/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser._

import scala.scalajs.js

object PhaserKeys {
  def isFireDown(game: Game): Boolean = {
    val k = game.input.keyboard
    k.isDown(0x0D) || k.isDown(' ') || k.isDown('M')
  }
}


class ScaleManagerCompatibility extends js.Object {
  var supportsFullScreen: Boolean = _
  var orientationFallback: Boolean = _
  var noMargins: Boolean = _
  var scrollTo: Point = _
  var forceMinimumDocumentHeight: Boolean = _
  var canExpandParent: Boolean = _
  var clickTrampoline: String = _
}

object PhaserButton {

  val FrameBasic = 0
  val FrameMax = 3
  val FrameMin = 6
  val FrameRotLeft = 9
  val FrameRotRight = 10
  val FrameThrust = 11
  val FramePlay = 12
  val FrameFire = 13
  val FrameExit = 14
  val FrameGrid = 15
  val FrameLeft = 16
  val FrameRight = 17
  val FrameRetry = 18
  val FramePause = 19
  val FrameButton = 20

  private var pauseMenu: Group = _

  def add(game: Game, x: Double, y: Double, text: String, alpha: Double = 0.75,
          group: Group = null, scale: Double = 1.5, frame: Int = FrameBasic, textFrame: Int = -1): Button = {
    val button = game.add.button(x,y, GlobalRes.ButtonId, null, null, frame, frame+1, frame+2, frame+1)
    button.scale.set(scale,scale)
    button.anchor.set(0.5,0.5)

    val obj = if (text.nonEmpty) {
      val t: BitmapText = game.add.bitmapText(x, y, GlobalRes.FontId, text, 32 * scale)
      t.align = "center"
      t.anchor.set(0.5, 0.5)
      t.alpha = alpha
      t
    }
    else {
      val t = game.add.sprite(x,y,GlobalRes.ButtonId, textFrame)
      t.scale.set(scale,scale)
      t.anchor.set(0.5, 0.5)
      t.alpha = alpha
      t
    }
    if (group!=null) {
      group.add(button)
      group.add(obj)
    }
    button
  }

  def addMinMax(game: Game): Unit = {
    if (game.scale.compatibility.asInstanceOf[ScaleManagerCompatibility].supportsFullScreen) {
      game.scale.onFullScreenChange.dispose() // clear all old listeners
      val frame = if (game.scale.isFullScreen) FrameMin else FrameMax
      val button2 = PhaserButton.add(game, 50, 50, " ", scale = 0.4, frame = frame)
      game.scale.onFullScreenChange.add(() => if (game.scale.isFullScreen) {
        button2.setFrames(FrameMin, FrameMin+1, FrameMin+2)
      } else {
        button2.setFrames(FrameMax, FrameMax+1, FrameMax+2)
      }, null, 1)
      button2.events.onInputUp.add(() => if (game.scale.isFullScreen) {
        game.scale.stopFullScreen()
      } else {
        game.scale.startFullScreen()
      }, null, 1)
    }
  }

  def addExit(game: Game, x: Double, y: Double, scale: Double = 1.0, group: Group = null): Button = {
    val button = PhaserButton.add(game, x,y, "", textFrame = PhaserButton.FrameExit, scale = scale, group = group)
    button.events.onInputUp.add(() => gotoMenu(game), null, 1)
    button
  }
  def addLevels(game: Game, x: Double, y: Double, scale: Double = 1.0, group: Group = null): Button = {
    val button = PhaserButton.add(game, x,y, "", textFrame=PhaserButton.FrameGrid, scale = scale, group = group)
    button.events.onInputUp.add(() => gotoLevels(game), null, 1)
    button
  }
  def addRetry(game: Game, x: Double, y: Double, scale: Double = 1.0, group: Group = null): Button = {
    val button = PhaserButton.add(game, x,y, "", textFrame=PhaserButton.FrameRetry, scale = scale, group = group)
    button.events.onInputUp.add(() => gotoRetry(game), null, 1)
    button
  }

  def addPause(game: Game, x: Double, y: Double, scale: Double = 0.6, group: Group = null): Button = {
    val button = PhaserButton.add(game, x,y, "", textFrame=PhaserButton.FramePause, scale = scale, group = group)
    button.events.onInputUp.add(() => game.paused = true, null, 1)
    button
  }

  def createPauseMenu(game: Game, touchControls: TouchControls): Group = {
    val step = 100
    val y = 64
    var x = game.width - 64
    val scale = 0.6
    val group = game.add.group(name = "pausemenu")
    val button = PhaserButton.add(game, x,y, "", textFrame=PhaserButton.FramePlay, scale = scale, group = group)
    group.add(button)
    group.add(PhaserButton.addRetry(game, x-step,y, scale = scale, group = group))
    group.add(PhaserButton.addLevels(game, x-2*step,y, scale = scale, group = group))
    group.add(PhaserButton.addExit(game, x-3*step,y, scale = scale, group = group))

    group.forEach((button: Button) => button.events.onInputUp.add(() => {
      game.paused = false
    }, null, 1), null, false)

    if (touchControls.touchButtons.visible) {
      val buttonLayout = PhaserButton.add(game, x-4*step, y, "", textFrame = PhaserButton.FrameButton, scale = scale, group = group)
      buttonLayout.events.onInputUp.add(() => touchControls.nextLayout(), null, 1)
      group.add(buttonLayout)
    }
    group
  }

  def gotoLevels(game: Game): Unit = {
    game.state.start("levels", args = "gameover", clearCache = false, clearWorld = true)
  }
  def gotoRetry(game: Game): Unit = {
    game.state.start("play", args = "restore", clearCache = false, clearWorld = true)
  }
  def gotoMenu(game: Game): Unit = {
    game.state.start("menu", args = "gameover", clearCache = false, clearWorld = true)
  }
}

object PhaserGraphics {
  def addBox(game: Game, bounds: Rectangle, lineColor: Int, lineWidth: Int, fillColor: Option[Int]): Graphics = {
    val gr = game.add.graphics(bounds.x,bounds.y)
    gr.lineStyle(lineWidth, lineColor, 1)
    if (fillColor.isDefined) gr.beginFill(fillColor.get, 0.75)
    gr.drawRect(0,0,bounds.width,bounds.height)
    gr.endFill()
    gr
  }
}