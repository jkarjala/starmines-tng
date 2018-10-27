package com.jpkware.smng

import com.definitelyscala.phaser.{Game, _}


class StateBoot(game: Game, options: Map[String,String]) {

  val state: State = State(init, preload, create, () => {}, () => {})

  def init(): Unit = {
    game.time.advancedTiming = true
    game.scale.fullScreenScaleMode = ScaleManager.SHOW_ALL
    game.scale.scaleMode = ScaleManager.SHOW_ALL
  }

  def preload(): Unit = {
    game.load.image("preloaderBar", "res/flame.png")
    game.load.image("ship-preload", "res/ship-preload.png")
  }

  def create(): Unit = {
    game.state.start("preloader", args = null, clearCache = false, clearWorld = false)
  }
}