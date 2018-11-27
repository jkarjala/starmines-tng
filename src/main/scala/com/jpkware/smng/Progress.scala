package com.jpkware.smng

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.JSON

class Progress extends js.Object {
  var maxLevel: Int = 1
  var maxBonusesCollected : Int = 0
  var highScore: Int = 0
}

object Progress {
  val LSProgressKey = "starmines-progress"

  val state: Progress = Progress()

  def apply(): Progress = {
    dom.window.localStorage.getItem(LSProgressKey) match {
      case item: String =>
        Logger.info(s"Loaded progress:$item")
        JSON.parse(item).asInstanceOf[Progress]
      case _ =>
        Logger.info(s"No local progress found")
        new Progress()
    }
  }

  def update(scores: ScoreState): Unit = {
    // Logger.info(s"$scores ${JSON.stringify(state)}")
    if (scores.level > state.maxLevel) {
      state.maxLevel = scores.level
    }
    if (scores.totalBonuses > state.maxBonusesCollected) {
      state.maxBonusesCollected = scores.totalBonuses
    }
    if (scores.score > state.highScore) {
      state.highScore = scores.score
    }
  }

  def save(progress: Progress): Unit = {
    val json: String = JSON.stringify(progress)
    Logger.info(s"Saved progress:$json")
    dom.window.localStorage.setItem(Progress.LSProgressKey, json)
  }

  def updateAndSave(scores: ScoreState): Unit = {
    update(scores)
    save(state)
  }
}
