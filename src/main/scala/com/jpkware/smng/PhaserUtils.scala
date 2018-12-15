package com.jpkware.smng

import com.definitelyscala.phaser._

object PhaserKeys {
  def isFireDown(game: Game): Boolean = {
    val k = game.input.keyboard
    k.isDown(0x0D) || k.isDown(' ') || k.isDown('M')
  }
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
    if (!game.device.iOS) {
      game.scale.onFullScreenChange.dispose() // clear all old listeners
      val frame = if (game.scale.isFullScreen) FrameMin else FrameMax
      val button2 = PhaserButton.add(game, 32, 32, " ", scale = 0.4, frame = frame)
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
}
