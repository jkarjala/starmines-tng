/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser._
import com.definitelyscala.phaser.Physics.Arcade.Body

import scala.scalajs.js

class ScoreState extends js.Object {
  var score: Int = 0
  var lives: Int = 5
  var level: Int = 1
  var bonusoidsCollected: Int = 0
  var totalBonusoids: Int = 0
  var timeBonus: Int = 0
  var stars: Int = 0
  var shipLevel: Int = 0
}

class Scorebox(game: Game, scores: ScoreState) extends Sprite(game, 0,0, Scorebox.ScoreboxId) {

  private var scoreText: BitmapText = _
  private var livesImage: Image = _
  private var levelText: BitmapText = _
  private var bonusText: BitmapText = _
  private var bonusoidText: BitmapText = _

  game.add.existing(this)
  position.set(game.width/2,game.height/2)
  anchor.set(0.5,0.5)
  game.physics.enable(this)
  body match { case b: Body => b.immovable = true}

  game.add.bitmapText(game.width/2,game.height/2-80, GlobalRes.FontId, "StarMines", 96).anchor.set(0.5,0.5)
  game.add.bitmapText(game.width/2,game.height/2-48, GlobalRes.FontId, "THE NEXT GENERATION", 28).anchor.set(0.5,0.5)

  game.add.bitmapText(game.width/2-280,game.height/2+16, GlobalRes.FontId, "SCORE:", 28)
  scoreText = game.add.bitmapText(game.width/2+280,game.height/2, GlobalRes.FontId, "", 48)
  scoreText.anchor.set(1,0)

  game.add.bitmapText(game.width/2-280,game.height/2+45+16, GlobalRes.FontId, "TIME BONUS:", 28)
  bonusText = game.add.bitmapText(game.width/2+280,game.height/2+45, GlobalRes.FontId, "", 48)
  bonusText.anchor.set(1,0)

  livesImage = game.add.image(game.width/2-25,game.height/2+110, Scorebox.ShipsId)
  livesImage.scale.set(0.5,0.5)
  livesImage.anchor.set(0,0.25)

  bonusoidText = game.add.bitmapText(game.width/2-280,game.height/2+110, GlobalRes.FontId, "", 28)
  bonusoidText.anchor.set(0,0)

  levelText = game.add.bitmapText(game.width/2+280,game.height/2+110, GlobalRes.FontId, "", 28)
  levelText.anchor.set(1,0)

  addToScore(0)
  addToLives(0)
  addToLevel(0)
  addToTimeBonus(0)
  addToBonusoidsCollected(0)

  private val timer = game.time.create(true)
  timer.loop(500, () => {
    addToTimeBonus(-500)
  }, null)
  timer.start(0)

  def addToBonusoidsCollected(delta: Int): Unit = {
    scores.bonusoidsCollected += delta
    scores.totalBonusoids += delta
    bonusoidText.setText(f"B'soids: ${scores.totalBonusoids}%d")
  }

  def addToLives(delta: Int): Unit = {
    scores.lives += delta
    cropRect = new Rectangle(0, 0, math.max(0, (scores.lives-1)*36), livesImage.height*2)
    livesImage.crop(cropRect)
  }

  def addToLevel(delta: Int): Unit = {
    scores.level += delta
    levelText.setText(f"Field: ${scores.level}%d")
  }

  def addToScore(delta: Int): Unit = {
    scores.score += delta
    if (scores.score<0) scores.score = 0
    scoreText.setText(f"${scores.score}%d")
  }

  def addToTimeBonus(delta: Int): Unit = {
    if (scores.timeBonus + delta < 0) {
      scores.timeBonus = 0
      bonusText.setText(f"${scores.timeBonus}%d")
    }
    else {
      scores.timeBonus += delta
      bonusText.setText(f"${scores.timeBonus}%d")
    }
  }
}

object Scorebox {
  val ScoreboxId = "scorebox"
  val ShipsId = "ships"
  def InitialScore = new ScoreState
  def preloadResources(game: Game): Unit = {
    game.load.image(ScoreboxId, "res/scorebox.png")
    game.load.image(ShipsId, "res/ships.png")
  }
}
