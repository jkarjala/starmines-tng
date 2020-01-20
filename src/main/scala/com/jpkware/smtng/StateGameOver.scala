/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2020 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{Game, Rectangle, State}

class StateGameOver(game: Game, options: Map[String,String]) extends State {

  var keyDown: Boolean = _
  var scoresDisplayed: Boolean = _

  override def create(): Unit = {

    game.add.bitmapText(game.width/2,250, GlobalRes.FontId, "Game Over", 128).anchor.set(0.5,0.5)
    scoresDisplayed = false
    PhaserButton.addExit(game, game.width/2-200,game.height-200)
    keyDown = PhaserKeys.isFireDown(game)
    PhaserButton.addRetry(game, game.width/2,game.height-200)
    PhaserButton.addLevels(game, game.width/2+200,game.height-200)
  }

  override def update(): Unit = {
    if (!Progress.postPending && !scoresDisplayed) {
      // wait until the scores have been posted to ensure the latest game results are included
      PhaserGraphics.addBox(game, new Rectangle(game.width-620,game.height/2-160,590,320), 0xFFFFFF, 2, Some(0x202020))

      val fieldScoreText = game.add.bitmapText(game.width-600,game.height/2-145, GlobalRes.FontId, "", 24)
      val level = StatePlay.scores.level
      Progress.fetchScores(Some(level), 9, (scores: Seq[HighScore]) => {
        fieldScoreText.text = s"Field $level High Scores:\n"+Progress.formatScores(scores)
      })

      PhaserGraphics.addBox(game, new Rectangle(30,game.height/2-160,590,320), 0xFFFFFF, 2, Some(0x202020))
      val globalScoreText = game.add.bitmapText(50, game.height/2-145, GlobalRes.FontId, "", 24)
      Progress.fetchScores(None, 9, (scores: Seq[HighScore]) => {
        globalScoreText.text = s"Game High Scores:\n"+Progress.formatScores(scores)
      })
      scoresDisplayed = true
    }
    if (keyDown) keyDown = !PhaserKeys.isFireDown(game)
    if (!keyDown && PhaserKeys.isFireDown(game)) PhaserButton.gotoRetry(game)
    if (game.input.keyboard.isDown(27)) PhaserButton.gotoMenu(game)
    if (PhaserKeys.isRetryDown(game)) PhaserButton.gotoRetry(game)
    if (PhaserKeys.isLevelsDown(game)) PhaserButton.gotoLevels(game)
  }
}