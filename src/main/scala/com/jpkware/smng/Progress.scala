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

  def apply(): Progress = {
    dom.window.localStorage.getItem(LSProgressKey) match {
      case item: String =>
        Logger.info(s"Loaded progress:$item")
        JSON.parse(item).asInstanceOf[Progress]
      case _ => new Progress()
    }
  }

  def save(progress: Progress): Unit = {
    val json: String = JSON.stringify(progress)
    Logger.info(s"Saved progress:$json")
    dom.window.localStorage.setItem(Progress.LSProgressKey, json)
  }
}
