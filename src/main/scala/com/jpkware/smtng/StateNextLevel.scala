/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{BitmapText, Game, Sound, State}
import org.scalajs.dom.raw.Element

import scala.scalajs.js

class StateNextLevel(game: Game, options: Map[String,String]) extends State {

  var keyDown: Boolean = _
  var result: String = _
  var resultText: BitmapText = _
  var sfxTick: Sound = _
  var saved: Boolean = _

  override def init(args: js.Any*): Unit = {
    args.headOption match {
      case str: Some[js.Any] =>
        Logger.info(s"init ${str.get}")
        result = str.get.asInstanceOf[String]
      case _ => Logger.info(s"Unknown args: $args")
    }
  }

  override def create(): Unit = {

    val button = PhaserButton.add(game, game.width/2,game.height-200, "", textFrame=PhaserButton.FramePlay)
    button.events.onInputUp.add(gotoPlay _, null, 1)
    keyDown = PhaserKeys.isFireDown(game)

    resultText = game.add.bitmapText(game.width/2,250, GlobalRes.FontId, "Collecting time bonus...", 48)
    resultText.anchor.set(0.5,0.5)

    sfxTick = game.add.audio(StateNextLevel.SfxTick)
    saved = false

    if (StatePlay.scores.timeBonus>0) {
      val timer = game.time.create(true)
      timer.loop(150, () => {
        if (StatePlay.scores.timeBonus>2000)
          StatePlay.scorebox.addToScore((2000).toInt)
        else
          StatePlay.scorebox.addToScore((StatePlay.scores.timeBonus).toInt)
        StatePlay.scorebox.addToTimeBonus((-2000).toInt)
        sfxTick.play()
        if (StatePlay.scores.timeBonus==0) {
          timer.stop(true)
        }
      }, null)
      timer.start(0)
    }
  }

  override def update(): Unit = {
    if (StatePlay.scores.timeBonus>0) return
    else {
      resultText.text = result
      if (!saved) {
        game.add.bitmapText(game.width / 2, game.height - 50, GlobalRes.FontId, "Saved a Checkpoint", 40).anchor.set(0.5, 0.5)
        Progress.saveCheckpoint(StatePlay.scores)
        saved = true
      }
    }

    if (keyDown) keyDown = !PhaserKeys.isFireDown(game)
    if (!keyDown && PhaserKeys.isFireDown(game)) gotoPlay()
  }

  def gotoPlay(): Unit = {
    game.state.start("play", args = "nextlevel", clearCache = false, clearWorld = true)
  }

}

object StateNextLevel {
  def SfxTick = "sfx:tick"

  def preloadResources(game: Game): Unit = {
    game.load.audio(SfxTick, "res/tick.wav")
  }
}