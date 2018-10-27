package com.jpkware.smng

import com.definitelyscala.phaser.{Button, Game, Keyboard, State}

import scala.scalajs.js

// Javascript literal object generator for Phaser State
object PhaserState {
  def apply(init: (js.Array[String]) => Unit, preload: () => Unit,  create: () => Unit, update: () => Unit, render: () => Unit, destroy: () => Unit): State = {
    js.Dynamic.literal(init = init, preload = preload, create = create, update = update, render = render, destroy = destroy).asInstanceOf[State]
  }

  def apply(init: (js.Array[String]) => Unit, preload: () => Unit,  create: () => Unit, update: () => Unit, render: () => Unit): State = {
    js.Dynamic.literal(init = init, preload = preload, create = create, update = update, render = render).asInstanceOf[State]
  }

  def apply(preload: () => Unit,  create: () => Unit, update: () => Unit, render: () => Unit): State = {
    js.Dynamic.literal(init = () => {}, preload = preload, create = create, update = update, render = render).asInstanceOf[State]
  }

  def apply(preload: () => Unit,  create: () => Unit, update: () => Unit): State = {
    js.Dynamic.literal(init = () => {}, preload = preload, create = create, update = update, render = () => {}).asInstanceOf[State]
  }
}

object PhaserKeys {
  def isFireDown(game: Game) = {
    val k = game.input.keyboard
    k.isDown(0x0D) || k.isDown(' ') || k.isDown('M')
  }
}

object PhaserButton {
  def add(game: Game, x: Double, y: Double, text: String, alpha: Double = 0.75): Button = {
    val button = game.add.button(x,y, "button", null, null, 0, 1, 0, 1)
    button.scale.set(2,2)
    button.anchor.set(0.5,0.5)
    val t = game.add.bitmapText(x,y, "font", text, 48)
    t.anchor.set(0.5,0.5)
    t.alpha = alpha
    button
  }
}