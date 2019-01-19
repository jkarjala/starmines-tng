/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser._

import scala.scalajs.js

class StateLevels(game: Game, options: Map[String,String]) extends State {

  private lazy val gridW = game.width/4
  private lazy val gridH = game.height/4

  var initialStartLevel: Int = 0
  var currentStartLevel: Int = _

  var levelInfoText: BitmapText = _
  var levelScoresText: BitmapText = _
  var levelInfoGroup: Group = _
  var gridGroup: Group = _

  override def create(): Unit = {
    if (initialStartLevel==0 || initialStartLevel>Progress.state.maxLevel) initialStartLevel = Progress.state.maxLevel/64*64 + 1
    createGrid(initialStartLevel, Progress.state.maxLevel, Progress.state.stars.toMap)
  }

  override def update(): Unit = {
    // XXX add keyboard controls to move around?
    if (selectedLevel>0) {
      if (PhaserKeys.isFireDown(game) || PhaserKeys.isRetryDown(game)) startGame(selectedLevel)
    }
    else {
      if (game.input.keyboard.isDown(27)) gotoMenu()
    }
  }

  def createGrid(startLevel: Int, maxLevel: Int, starMap: Map[String, Int]): Unit = {

    var level = startLevel
    currentStartLevel = startLevel
    game.world.removeAll(destroy = true)
    gridGroup = game.add.group(name = "gridButtons")
    for (y <- 0 to 3; x <- 0 to 3) {
        val bg = StarMinesNG.addBackground(game, level, x * gridW, y * gridH)
        bg.scale.set(gridW / bg.width, gridH / bg.height)
        gridGroup.add(bg)
        if (level > maxLevel) {
          bg.alpha = 0.25
        }
        val deltaX = gridW / 5
        val marginX = x * gridW + deltaX
        val marginY = y * gridH + gridH / 2
        for (bx <- 0 to 3) {
          if (level <= maxLevel) {
            val b = PhaserButton.add(game, marginX + deltaX * bx, marginY, level.toString, scale = 0.7, group=gridGroup)
            b.events.onInputUp.add( (o: Any, p: Pointer, isOver: Boolean, lvl: Int) => showInfo(lvl), null, 1, args=level)
            val starCount = starMap.getOrElse(level.toString, 0)
            val stars = "*" * starCount
            val t = game.add.bitmapText(marginX + deltaX * bx, marginY + 32, GlobalRes.FontId, stars, 32)
            t.anchor.set(0.5,0.5)
            gridGroup.add(t)
          }
          else {
            val t = game.add.bitmapText(marginX + deltaX * bx, marginY, GlobalRes.FontId, level.toString, 32 * 0.7)
            t.alpha = 0.25
            t.anchor.set(0.5,0.5)
            gridGroup.add(t)
          }
          level += 1
        }
    }

    if (currentStartLevel > 64) {
      val button = PhaserButton.add(game, 40, game.height / 2, "", textFrame = PhaserButton.FrameLeft,
        scale = 0.5, group = gridGroup)
      button.events.onInputUp.add(() => {
        createGrid(currentStartLevel - 64, maxLevel, starMap)
      }, null, 1)
    }

    if (currentStartLevel + 64 < maxLevel) {
      val button = PhaserButton.add(game, game.width - 40, game.height / 2, "", textFrame = PhaserButton.FrameRight,
        scale = 0.5, group = gridGroup)
      button.events.onInputUp.add(() => {
        createGrid(currentStartLevel + 64, maxLevel, starMap)
      }, null, 1)
    }
    else {
      val s = game.add.sprite(game.width - 40, game.height / 2, GlobalRes.ButtonId, PhaserButton.FrameRight,
        group = gridGroup)
      s.scale.set(0.5,0.5)
      s.anchor.set(0.5,0.5)
      s.alpha = 0.25
    }

    val button = PhaserButton.addExit(game, game.width - 40, 40, scale = 0.5, group = gridGroup)
    button.events.onInputUp.add(() => gotoMenu(), null, 1)

    levelInfoGroup = createInfo(game)
  }

  def createInfo(game: Game): Group = {
    val x = 100
    val y = 50
    val step = 50
    val group = game.add.group(name="levelInfo")

    val bg = game.add.sprite(0,0, GlobalRes.MenuBg)
    bg.scale.set(2,2)
    group.add(bg)

    group.add(PhaserGraphics.addBox(game, new Rectangle(x,y,game.width-2*x,game.height-2*y), 0xFFFFFF, 2, Some(0x202020)))
    levelInfoText = game.add.bitmapText(x+step/2,y+step/2,GlobalRes.FontId, "")
    group.add(levelInfoText)
    levelScoresText = game.add.bitmapText(game.width/2,y+step/2,GlobalRes.FontId, "")

    val button = PhaserButton.add(game, game.width/2-100, game.height-150, "", scale = 1.0,
      textFrame=PhaserButton.FrameRetry, group = group)
    val buttonBack = PhaserButton.add(game, game.width/2+100, game.height-150, "", scale = 1.0,
      textFrame=PhaserButton.FrameExit, group = group)

    buttonLeft = PhaserButton.add(game, x+step, game.height - 100, "", textFrame = PhaserButton.FrameLeft,
      scale = 0.5, group = group)
    buttonLeft.events.onInputUp.add(() => { showInfo(selectedLevel-1) }, null, 1)

    buttonRight = PhaserButton.add(game, game.width - x-step, game.height - 100, "", textFrame = PhaserButton.FrameRight,
      scale = 0.5, group = group)
    buttonRight.events.onInputUp.add(() => { showInfo(selectedLevel+1) }, null, 1)

    button.events.onInputUp.add(() => {
      hideInfo()
      startGame(selectedLevel)
    }, null, 1)

    buttonBack.events.onInputUp.add(() => {
      hideInfo()
    }, null, 1)

    group.add(levelScoresText)
    group.visible = false
    group
  }

  def hideInfo(): Unit = {
    levelInfoGroup.visible = false
    gridGroup.visible = true
  }

  var buttonLeft: Button = _
  var buttonRight: Button = _
  var selectedLevel: Int = -1

  def showInfo(level: Int): Unit = {
    selectedLevel = level
    buttonLeft.visible = selectedLevel > 1
    buttonRight.visible = selectedLevel < Progress.state.maxLevel
    levelInfoGroup.visible = true
    gridGroup.visible = false
    val cp = Progress.getCheckpoint(level)

    val fieldInfo = if (cp.score==0) s"You have not yet completed field $level!\n\n"
    else s"Your statistics at Field $level:\n\n" +
      s" Score so far:\n  ${cp.score}\n" +
      s" Field Bonusoids:\n  ${cp.bonusoidsCollected} / ${BonusManager.bonusoidsOnLevel(level)}\n" +
      s" Total Bonusoids:\n  ${cp.totalBonusoids} / ${BonusManager.maxBonusoidsOnLevel(level)}\n" +
      s" Ships left:\n  ${cp.lives}\n"

    val info =  fieldInfo +
      s"\nYour Total Game Statistics:\n\n" +
      s" High Score:\n  ${Progress.state.highScore}\n" +
      s" Total Bonusoids:\n  ${Progress.state.maxBonusoids}\n" +
      s" Playing Since:\n  ${new js.Date(Progress.state.playTime.get).toDateString()}\n"

    levelInfoText.setText(info)
    levelScoresText.setText("Retrieving Field High Scores...")
    Progress.fetchScores(Some(level), 18, (scores: Seq[HighScore]) => {
      levelScoresText.setText(s"Field $level High Scores:\n"+Progress.formatScores(scores))
    })

  }

  def startGame(level: Int): Unit = {
    initialStartLevel = currentStartLevel
    game.state.start("play", args = level.toString, clearCache = false, clearWorld = true)
  }

  def gotoMenu(): Unit = {
    game.state.start("menu", args = "levels", clearCache = false, clearWorld = true)
  }
}