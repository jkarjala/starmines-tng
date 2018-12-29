/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.{BitmapText, Game, Sprite, State}
import org.scalajs.dom.raw.Element

import scala.collection.mutable

class StateMenu(game: Game, options: Map[String,String]) extends State {

  var infoTexts: mutable.Buffer[BitmapText] = mutable.Buffer()
  var scoreTexts: mutable.Buffer[BitmapText] = mutable.Buffer()
  var scores: Seq[HighScore] = Seq()
  var scoresShown: Boolean = false
  val ScoreListLen = 10

  override def preload(): Unit = {
  }

  override def create(): Unit = {
    game.add.sprite(0,0, GlobalRes.MenuBg).scale.set(2,2)

    GlobalRes.drawLogo(game)

    val help = if (game.device.desktop || options.contains("touch")) "Control your ship with arrow keys and space, or z,x,n,m, or mouse"
    else "Use the touch buttons to control your ship"

    infoTexts.append(game.add.bitmapText(game.width/2,game.height-400, GlobalRes.FontId, s"Welcome ${Progress.state.name}!", 40))
    infoTexts.append(game.add.bitmapText(game.width/2,game.height-250, GlobalRes.FontId,
      "Collect all Bonusoids for maximum score and ship upgrades", 32))
    infoTexts.append(game.add.bitmapText(game.width/2,game.height-200, GlobalRes.FontId, help, 32))
    infoTexts.append(game.add.bitmapText(game.width/2,game.height-50, GlobalRes.FontId, "Copyright 2018 Jari.Karjala@iki.fi", 32))
    infoTexts.foreach(_.anchor.set(0.5,0.5))

    (1 to ScoreListLen).foreach(i => scoreTexts.append(game.add.bitmapText(game.width/2-270,game.height-440+i*40, GlobalRes.FontId, "", 32)))
    scoreTexts.append(game.add.bitmapText(game.width/2-290,game.height-460, GlobalRes.FontId, "Top 10 Scores and Players:", 40))
    scoreTexts.foreach(_.anchor.set(0,0.5))

    showTexts()

    val (b1x,b2x,b3x) = if (Progress.hasCheckpoint) (-200,0,200) else (-100, 0, 100)
    val buttonY = game.height/2-60

    val button = PhaserButton.add(game, game.width/2+b1x, buttonY, "", scale = 1.0, textFrame=PhaserButton.FramePlay)
    button.events.onInputUp.add(startGame _, null, 1)

    if (Progress.hasCheckpoint) PhaserButton.addRetry(game, game.width/2+b2x, buttonY)

    PhaserButton.addLevels(game, game.width/2+b3x, buttonY)

    PhaserButton.addMinMax(game)

    fetchHighScores()
    val timer = game.time.create(true)
    timer.loop(10000, () => {
      if (scoresShown) showTexts() else showScores()
    }, null)
    timer.start(1000)
  }

  override def update(): Unit = {
    if (PhaserKeys.isFireDown(game)) startGame()
  }

  def showTexts(): Unit = {
    infoTexts.foreach(_.visible = true)
    scoreTexts.foreach(_.visible = false)
    scoresShown = false
  }

  def showScores(): Unit = {
    infoTexts.foreach(_.visible = false)
    if (scores.nonEmpty && !scoresShown) {
      scores.zipWithIndex.foreach {
        case (score, index) =>
          scoreTexts(index).text = f"${index+1}%3d.  ${score.score}%08d  ${score.name}"
      }
      scoreTexts.foreach(_.visible = true)
      fetchHighScores()
      scoresShown = true
    }
  }

  def fetchHighScores(): Unit = {
    Progress.fetchScores(None, ScoreListLen, (scores: Seq[HighScore]) => {this.scores = scores})
  }
  def startGame(): Unit = {
    Progress.resetCheckpoint()
    game.state.start("play", args = "start", clearCache = false, clearWorld = true)
  }
}