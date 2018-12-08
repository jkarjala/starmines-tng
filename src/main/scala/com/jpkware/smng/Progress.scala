package com.jpkware.smng

import org.scalajs.dom
import org.scalajs.dom.{Event, XMLHttpRequest}

import scala.scalajs.js
import scala.scalajs.js.{Dictionary, JSON}
import scala.collection.mutable

class Progress extends js.Object {
  var maxLevel: Int = 1
  var maxBonusoids : Int = 0
  var highScore: Int = 0
  var stars: js.Dictionary[Int] = Dictionary()
  var startTime: js.UndefOr[Double] = new js.Date().getTime()
  var playTime: js.UndefOr[Double] = new js.Date().getTime()
  var id: js.UndefOr[String] = ""
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
        if (js.isUndefined(p.startTime)) p.startTime = new js.Date().getTime()
        if (js.isUndefined(p.id)) p.id = ""
        p
      case _ =>
        Logger.info(s"No local progress found")
        new Progress()
    }
  }

  def update(scores: ScoreState): Unit = {
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
    if (scores.score==0) {
      // Assume game started when score is 0
      state.playTime = new js.Date().getTime()
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
    postScores(state, scores)
  }

  def postScores(progress: Progress, scores: ScoreState): Unit = {
    val sd = progress.startTime.get
    val pd = progress.playTime.get
    val tsv = sd +
      "\t" + pd +
      "\t" + scores.level +
      "\t" + scores.stars +
      "\t" + scores.score +
      "\t" + scores.lives +
      "\t" + scores.timeBonus +
      "\t" + scores.bonusoidsCollected +
      "\t" + scores.totalBonusoids +
      // Once in production, add new items here!
      ""
    postData(progress.id.get, tsv, (str: String) => { progress.id = str })
  }
  private val hostUrl = "https://jpkware.com"

  def postData(path: String, data: String, callback: (String) => Unit): Unit = {
    val xhr = new XMLHttpRequest()

    Logger.info(s"XHR POST $data")
    xhr.open("POST", s"$hostUrl/smtng.php/$path", async = true)
    xhr.setRequestHeader("Content-Type", "application/tsv")
    xhr.onreadystatechange = { (e: Event) => { // Call a function when the state changes.
      if (xhr.status == 200) {
        if (xhr.readyState==4) {
          Logger.info(s"XHR POST response '${xhr.response}'")
          callback(xhr.response.toString)
        }
      }
      else {
        Logger.warn(s"XHR ${xhr.readyState} ${xhr.status} ${xhr.response}")
      }
    }}
    xhr.send(data)
  }
}
