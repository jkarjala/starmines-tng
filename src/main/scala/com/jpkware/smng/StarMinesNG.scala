package com.jpkware.smng

import com.definitelyscala.phaser.Game
import com.definitelyscala.phaser._
import org.scalajs.dom
import org.scalajs.dom.raw.Element

import scala.scalajs.js
import scala.util.Random


object StarMinesNG {
  val rnd = new Random(42)

  def main(args: Array[String]): Unit = {

    val parent: Element = dom.document.getElementById("game")
    val status: Element = dom.document.getElementById("status")
    val hash = dom.document.location.hash
    val options: Map[String, String] = if (hash==null || hash.isEmpty) Map() else {
      val s = hash.tail.split(',')
      s.map(o => {
        val os = o.split('=')
        if (os.length>1) os(0) -> os(1) else os(0) -> "true"
      }).toMap
    }
    val mode = if (options.contains("webgl")) Phaser.WEBGL else Phaser.CANVAS
    val game = new Game(1920, 1080, mode, parent)
    game.state.add("boot", new StateBoot(game, options).state)
    game.state.add("preloader", new StatePreload(game, options).state)
    game.state.add("menu", new StateMenu(game, options, status).state)
    game.state.add("play", new StatePlay(game, options, status).state)
    game.state.add("gameover", new StateGameOver(game, options, status).state)
    game.state.start("boot", args = js.Array[String](), clearWorld = true, clearCache = false)
  }
}
