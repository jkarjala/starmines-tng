package com.jpkware.smng

import com.definitelyscala.phaser.{Button, Game, Group}

class TouchControls(game: Game) {
  val touchButtons: Group = game.add.group()
  touchButtons.visible = false

  var rotateRight = false
  var rotateLeft = false
  var rotateStop = false
  var thrust = false
  var fire = false

  def isEnabled: Boolean = touchButtons.visible

  def enable(): Unit = {
    if (touchButtons.countLiving()==0) addTouchButtons()
    touchButtons.visible = true
    rotateRight = false
    rotateLeft = false
    rotateStop = false
    thrust = false
    fire = false
  }

  def disable(): Unit = {
    touchButtons.visible = false
  }

  def addTouchButtons(): Unit = {
    val radius = 128
    val buttonY = game.height - radius
    addTouchButton(radius, buttonY, "CCW", () => {
      rotateLeft = true
    }, () => {
      rotateLeft = false
      rotateStop = true
    })
    addTouchButton(radius*3, buttonY, "CW", () => {
      rotateRight = true
    }, () => {
      rotateRight = false
      rotateStop = true
    })
    addTouchButton(game.width - radius, buttonY, "Fire", () => {
      fire = true
    }, () => {
      fire = false
    })
    addTouchButton(game.width - radius*4 + radius, buttonY, "Thrust", () => {
      thrust = true
    }, () => {
      thrust = false
    })
  }

  def addTouchButton(x: Double, y: Double, text: String, down: () => Unit, up: () => Unit): Button = {
    val button = PhaserButton.add(game, x, y, text, 0.2, touchButtons)
    button.events.onInputOver.add(down, null, 1)
    button.events.onInputOut.add(up, null, 1)
    button.events.onInputDown.add(down, null, 1)
    button.events.onInputUp.add(up, null, 1)
    button
  }

}
