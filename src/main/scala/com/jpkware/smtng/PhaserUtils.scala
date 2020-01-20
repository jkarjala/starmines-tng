/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2020 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser._
import org.scalajs.dom

import scala.scalajs.js

object PhaserKeys {
  def isFireDown(game: Game): Boolean = {
    val k = game.input.keyboard
    k.isDown(0x0D) || k.isDown(' ') || k.isDown('M')
  }
  def isRetryDown(game: Game): Boolean = {
    val k = game.input.keyboard
    k.isDown('R')
  }
  def isLevelsDown(game: Game): Boolean = {
    val k = game.input.keyboard
    k.isDown('F') || k.isDown('L')
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
  val FrameVol0 = 21
  val FrameVol1 = 22
  val FrameVol2 = 23

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
      buttonSprite(game, x,y, alpha, scale, textFrame)
    }
    if (group!=null) {
      group.add(button)
      group.add(obj)
    }
    button
  }

  def buttonSprite(game: Game, x: Double, y: Double, alpha: Double = 0.75,
                   scale: Double = 1.5, textFrame: Int = -1): Sprite = {
    val t = game.add.sprite(x,y,GlobalRes.ButtonId, textFrame)
    t.scale.set(scale,scale)
    t.anchor.set(0.5, 0.5)
    t.alpha = alpha
    t
  }

  val LSVolume = "starmines-volume"
  private var currentVolume = dom.window.localStorage.getItem(LSVolume) match {
    case item: String =>
      item.toInt
    case _ =>
      2
  }

  def addVolume(game: Game, x: Double, y: Double, group: Option[Group] = None, scale: Double = 0.6): Button = {
    val alpha = 0.75
    val volumeStep = 0.3
    val volumeSprites = Array(
      buttonSprite(game, x,y, alpha, scale, PhaserButton.FrameVol0),
      buttonSprite(game, x,y, alpha, scale, PhaserButton.FrameVol1),
      buttonSprite(game, x,y, alpha, scale, PhaserButton.FrameVol2)
    )
    volumeSprites.foreach(s => {
      s.alpha = 0
      group.map(_.add(s))
    })
    volumeSprites(currentVolume).alpha = alpha
    game.sound.volume = volumeStep*currentVolume
    val button = PhaserButton.add(game, x,y, " ", group = group.orNull, scale = scale)

    def volumeHandler(): Unit = {
      volumeSprites(currentVolume).alpha = 0
      currentVolume -= 1
      if (currentVolume<0) currentVolume = volumeSprites.length-1
      volumeSprites(currentVolume).alpha = alpha
      dom.window.localStorage.setItem(LSVolume, currentVolume.toString)
      game.sound.volume = volumeStep*currentVolume
    }

    val key = game.input.keyboard.addKey('V')
    key.onDown.add(volumeHandler _, null, 1)
    button.events.onInputUp.add(volumeHandler _, null, 1)
    button
  }

  def addMinMax(game: Game): Unit = {
    if (game.scale.compatibility.asInstanceOf[ScaleManagerCompatibility].supportsFullScreen) {
      game.scale.onFullScreenChange.dispose() // clear all old listeners
      val frame = if (game.scale.isFullScreen) FrameMin else FrameMax
      val button2 = PhaserButton.add(game, 55, 55, " ", scale = 0.4, frame = frame)
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
    val x = game.width - 64
    val scale = 0.6
    val group = game.add.group(name = "pausemenu")
    val button = PhaserButton.add(game, x,y, "", textFrame=PhaserButton.FramePlay, scale = scale, group = group)
    group.add(button)
    group.add(PhaserButton.addRetry(game, x-step,y, scale = scale, group = group))
    group.add(PhaserButton.addLevels(game, x-2*step,y, scale = scale, group = group))
    group.add(PhaserButton.addExit(game, x-3*step,y, scale = scale, group = group))

    group.forEach((button: Button) => button.events.onInputUp.add(() => {
      game.paused = false // Only these change states, the rest are controls during pause
    }, null, 1), null, false)

    group.add(PhaserButton.addVolume(game, x,y+step, scale = scale, group = Some(group)))

    if (touchControls.touchButtons.visible) {
      val buttonLayout = PhaserButton.add(game, x, y+2*step, "", textFrame = PhaserButton.FrameButton, scale = scale, group = group)
      buttonLayout.events.onInputUp.add(() => touchControls.nextLayout(), null, 1)
      group.add(buttonLayout)
    }
    group
  }

  def gotoLevels(game: Game): Unit = {
    StarMinesNG.shareButtonVisible(false)
    game.state.start("levels", args = "gameover", clearCache = false, clearWorld = true)
  }
  def gotoRetry(game: Game): Unit = {
    StarMinesNG.shareButtonVisible(false)
    game.state.start("play", args = "restore", clearCache = false, clearWorld = true)
  }
  def gotoMenu(game: Game): Unit = {
    StarMinesNG.shareButtonVisible(false)
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