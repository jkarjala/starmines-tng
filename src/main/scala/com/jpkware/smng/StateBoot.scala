package com.jpkware.smng

import com.definitelyscala.phaser.{Game, _}

import scala.scalajs.js


class StateBoot(game: Game, options: Map[String,String]) {

  val state: State = PhaserState(init, preload, create, () => {}, () => {})

  def init(args: js.Array[String]): Unit = {
    game.time.advancedTiming = true
    game.scale.fullScreenScaleMode = ScaleManager.SHOW_ALL
    game.scale.scaleMode = ScaleManager.SHOW_ALL
  }

  def preload(): Unit = {
    game.load.image("preloaderBar", "res/flame.png")
    game.load.image("ship-preload", "res/ship-preload.png")
  }

  def create(): Unit = {
    game.state.start("preloader", args = js.Array[String](), clearCache = false, clearWorld = false)
  }
}