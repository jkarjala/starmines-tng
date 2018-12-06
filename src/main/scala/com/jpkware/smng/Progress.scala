package com.jpkware.smng

import org.scalajs.dom

import scala.scalajs.js
import scala.scalajs.js.{Dictionary, JSON}
import scala.collection.mutable

class Progress extends js.Object {
  var maxLevel: Int = 1
  var maxBonusoids : Int = 0
  var highScore: Int = 0
  var stars: js.Dictionary[Int] = Dictionary()
  var startDate: js.UndefOr[js.Date] = new js.Date()
  var playDate: js.UndefOr[js.Date] = new js.Date()
}

object Progress {
  val LSProgressKey = "starmines-progress"

  val state: Progress = Progress()

  def apply(): Progress = {
    dom.window.localStorage.getItem(LSProgressKey) match {
      case item: String =>
        Logger.info(s"Loaded progress:$item")
        val p = JSON.parse(item).asInstanceOf[Progress]
        // old state may not have these
        if (js.isUndefined(p.stars)) p.stars = Dictionary()
        if (js.isUndefined(p.startDate)) p.startDate = new js.Date()
        p
      case _ =>
        Logger.info(s"No local progress found")
        new Progress()
    }
  }

  def update(scores: ScoreState): Unit = {
    Logger.info(s"$scores ${JSON.stringify(state)}")
    if (scores.level > state.maxLevel) {
      state.maxLevel = scores.level
    }
    if (scores.totalBonusoids > state.maxBonusoids) {
      state.maxBonusoids = scores.totalBonusoids
    }
    if (scores.score > state.highScore) {
      state.highScore = scores.score
    }

    val key = scores.level.toString
    val scalaStars: mutable.Map[String, Int] = state.stars
    scalaStars.get(key) match {
      case Some(old) =>
        Logger.info(s"stars $old")
        if (old < scores.stars) scalaStars(key) = scores.stars
      case None =>
        Logger.info(s"stars none")
        scalaStars(key) = scores.stars
    }
    state.playDate = new js.Date()
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
