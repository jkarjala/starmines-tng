/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

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
  var name: js.UndefOr[String] = ""
  var id: js.UndefOr[String] = ""
}
class Checkpoints extends js.Object {
  var scores: js.Dictionary[String] = Dictionary()
}

case class HighScore(field: Int, score: Int, name: String, bonusoids: Int)

object Progress {
  val LSProgressKey = "starmines-progress"
  val LSCheckpointKey = "starmines-checkpoint"
  val LSLevelCheckpointsKey = "starmines-level-checkpoints"

  val state: Progress = Progress()
  val checkpoints: Checkpoints = {
    dom.window.localStorage.getItem(LSLevelCheckpointsKey) match {
      case item: String =>
        Logger.info(s"Loaded level checkpoints:$item")
        JSON.parse(item).asInstanceOf[Checkpoints]
      case _ =>
        Logger.info(s"No level checkpoints found")
        new Checkpoints
    }
  }
  var levelStartTime: Long = _

  def apply(): Progress = {
    dom.window.localStorage.getItem(LSProgressKey) match {
      case item: String =>
        Logger.info(s"Loaded progress:$item")
        val p = JSON.parse(item).asInstanceOf[Progress]
        // old state may not have these
        if (js.isUndefined(p.stars)) p.stars = Dictionary()
        if (js.isUndefined(p.startTime)) p.startTime = new js.Date().getTime()
        if (js.isUndefined(p.name)) p.name = ""
        if (js.isUndefined(p.id)) p.id = ""
        p
      case _ =>
        Logger.info(s"No local progress found")
        new Progress()
    }
  }

  def saveName(name: String): Unit = {
    state.name = name
    save(state)
  }

  def saveCheckpoint(scores: ScoreState): Unit = {
    val json: String = JSON.stringify(scores)
    Logger.info(s"Saved checkpoint:$json")
    dom.window.localStorage.setItem(Progress.LSCheckpointKey, json)

    checkpoints.scores(scores.level.toString) = JSON.stringify(scores)
    val json2 = JSON.stringify(checkpoints)
    Logger.info(s"Saved level checkpoints:$json2")
    dom.window.localStorage.setItem(Progress.LSLevelCheckpointsKey, json2)
  }

  def getCheckpoint(level: Int): ScoreState = {
    checkpoints.scores.get(level.toString) match {
      case Some(s) =>
        Logger.info(s"Local checkpoint found for level $level: $s")
        JSON.parse(s).asInstanceOf[ScoreState]
      case _ =>
        Logger.info(s"NO local checkpoint found for level $level")
        val s = Scorebox.InitialScore
        s.level = level
        s
    }
  }

  def restoreCheckpoint(level: Option[Int]): ScoreState = {
    val scores = if (level.isEmpty) {
      dom.window.localStorage.getItem(LSCheckpointKey) match {
        case item: String =>
          Logger.info(s"Loaded active checkpoint:$item")
          JSON.parse(item).asInstanceOf[ScoreState]
        case _ =>
          Logger.info(s"No active checkpoint found")
          Scorebox.InitialScore
      }
    }
    else {
      val lvl = math.max(1, level.get)
      val scores = getCheckpoint(lvl)
      if (level.get>0) saveCheckpoint(scores) // next retry starts from this point, except on level 1
      scores
    }
    scores.stars = 0
    scores.bonusoidsCollected = 0
    Logger.info(s"Scores:${JSON.stringify(scores)}")
    scores
  }

  def hasCheckpoint: Boolean = dom.window.localStorage.getItem(LSCheckpointKey) match {
    case _: String =>
      Logger.info("hasCheckpoint TRUE")
      true
    case _ => false
  }

  def resetCheckpoint(): Unit = {
    dom.window.localStorage.removeItem(Progress.LSCheckpointKey)
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
    if (scores.stars==0 && scores.lives>0) levelStartTime = new js.Date().getTime().toLong
  }

  def save(progress: Progress): Unit = {
    val json: String = JSON.stringify(progress)
    Logger.info(s"Saved progress:$json")
    dom.window.localStorage.setItem(Progress.LSProgressKey, json)
  }

  def updateAndSave(scores: ScoreState, debug: Boolean): Unit = {
    update(scores)
    save(state)
    postScores(state, scores, debug)
  }

  def postScores(progress: Progress, scores: ScoreState, debug: Boolean): Unit = {
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
      "\t" + progress.name +
      "\t" + (new js.Date().getTime().toLong - levelStartTime) +
      // Once in production, add new items here!
      ""
    postData(progress.id.get, tsv, {
      case Some(str) => progress.id = if (debug && !str.startsWith("!")) "!" + str else str
      case None => // XXX Should save the score for a later day posting...
    })
  }

  private val hostUrl = "https://jpkware.com"
  private val script = if (dom.document.location.host.startsWith("smtng.")) "smtng.php" else "smtng-dev.php"
  private val scriptUrl = s"$hostUrl/$script"

  var postPending: Boolean = false

  def serverRequest(op: String, url: String, data: Option[Any], callback: Option[String] => Unit): Unit = {
    val xhr = new XMLHttpRequest()
    xhr.open(op, url, async = true)
    xhr.setRequestHeader("Content-Type", "application/tsv")
    xhr.onreadystatechange = { (_: Event) => { // Call a function when the state changes.
      if (xhr.status == 200) {
        if (xhr.readyState==4) {
          Logger.info(s"XHR POST response '${xhr.response}'")
          callback(Some(xhr.response.toString))
        }
      }
      else {
        Logger.warn(s"XHR ${xhr.readyState} ${xhr.status} ${xhr.response}")
        callback(None)
      }
    }}
    if (data.isDefined) xhr.send(data.get.asInstanceOf[js.Any]) else xhr.send()
  }

  def postData(path: String, data: String, callback: (Option[String]) => Unit): Unit = {
    Logger.info(s"XHR POST $data")
    postPending = true
    serverRequest("POST", s"$scriptUrl/$path", Some(data), (result: Option[String]) => {
      postPending = false
      callback(result)
    })
  }

  def fetchScores(field: Option[Int], limit: Int, callback: (Seq[HighScore]) => Unit): Unit = {
    val url = if (field.isDefined) s"$scriptUrl?field=${field.get}&limit=$limit" else s"$scriptUrl?limit=$limit"
    serverRequest("GET", url, None, (result: Option[String]) => {
      val scores: Seq[HighScore] = result match {
        case Some(str) =>
          val lines = str.split("\n")
          lines.flatMap(line => {
            val values = line.split("\t")
            if (values.length < 3) None
            else Some(HighScore(values(0).toInt, values(1).toInt, values(2).replace("\"", ""), values(3).toInt))
          })
        case None => Seq()
      }
      callback(scores)
    })
  }

  private var buildVersion: String = "" // cached version if found

  def fetchBuild(callback: (String) => Unit): Unit = {
    if (buildVersion != "") {
      callback(buildVersion)
    }
    else {
      val href = dom.document.location.href
      val base = href.substring(0, href.lastIndexOf('/'))
      serverRequest("GET", s"$base/build.txt", None, {
        case Some(version) =>
          buildVersion = "v" + version.stripLineEnd
          callback(buildVersion)
        case None => callback("Version not found")
      })
    }
  }

  def formatScores(scores: Seq[HighScore]): String = {
    scores.zipWithIndex.map {
      case (score, index) => f"${index+1}%3d.  ${score.score}%08d  ${score.name}"
    }.mkString("\n")
  }
}
