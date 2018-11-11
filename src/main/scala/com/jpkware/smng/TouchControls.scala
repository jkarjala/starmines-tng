package com.jpkware.smng

import com.definitelyscala.phaser._

class TouchControls(game: Game, stick: Boolean) {
  val touchButtons: Group = game.add.group()
  touchButtons.visible = false

  var rotateRight = false
  var rotateLeft = false
  var rotateStop = false
  var thrust = false
  var fire = false

  val JoystickUp = 100.0
  var joystickRotation = JoystickUp

  def isEnabled: Boolean = touchButtons.visible

  def enable(): Unit = {
    if (touchButtons.countLiving()==0) addTouchButtons()
    touchButtons.visible = true
    rotateRight = false
    rotateLeft = false
    rotateStop = false
    thrust = false
    fire = false
    if (stick)
      addJoystick()
  }

  def disable(): Unit = {
    touchButtons.visible = false
  }

  def addTouchButtons(): Unit = {
    val radius = 128
    val buttonY = game.height - radius
    if (!stick) {
      addTouchButton(radius, buttonY, "CCW", () => {
        rotateLeft = true
      }, () => {
        rotateLeft = false
        rotateStop = true
      })
      addTouchButton(radius * 3 + 32, buttonY, "CW", () => {
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
      addTouchButton(game.width - radius * 3 - 32, buttonY, "Thrust", () => {
        thrust = true
      }, () => {
        thrust = false
      })
    }
    else
      addTouchButton(game.width - 256, game.height-256, "Fire", () => {
        fire = true
      }, () => {
        fire = false
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

  def addJoystick(): Unit = {
    val pos = new Point(256, game.height - 256)
    val cc = game.add.sprite(pos.x, pos.y, GlobalRes.ButtonId, 1)
    touchButtons.add(cc)
    cc.anchor.set(0.5, 0.5)
    cc.alpha = 0.25
    cc.scale.set(2,2)
    val c = game.add.sprite(pos.x, pos.y, GlobalRes.ButtonId)
    touchButtons.add(c)
    c.anchor.set(0.5, 0.5)
    c.alpha = 0.25
    c.scale.set(1.5, 1.5)
    c.inputEnabled = true
    c.input.enableDrag(false, false, false)
    c.events.onDragUpdate.add((c: Sprite, pointer: Pointer, x: Double, y: Double, snap: Point, fromStart: Boolean) => {
      val deltaX = (pos.x - x)
      val deltaY = (pos.y - y)
      val delta =  if (math.abs(deltaX) > math.abs(deltaY)) deltaX else deltaY
      if (math.abs(delta) > 10) joystickRotation = math.atan2(deltaY,deltaX)
      rotateLeft = false
      rotateRight = false
      // if (delta > 0) rotateLeft = true else if (delta < 0) rotateRight = true
    }, null, 1)
    c.events.onDragStop.add((c: Sprite, pointer: Pointer) => {
      c.x = pos.x
      c.y = pos.y
      joystickRotation = JoystickUp
      rotateLeft = false
      rotateRight = false
    }, null, 1)
  }
}
