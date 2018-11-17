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

  val JoystickReleased = 100.0  // Indicating "no action"
  var joystickRotation: Double = JoystickReleased
  var mouseRotation: Double = JoystickReleased
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
    val buttonY = game.height - radius - 32
    if (!stick) {
      addTouchButton(radius +32 , buttonY, "CCW", () => {
        rotateLeft = true
      }, () => {
        rotateLeft = false
        rotateStop = true
      })
      addTouchButton(radius * 3 + 64, buttonY, "CW", () => {
        rotateRight = true
      }, () => {
        rotateRight = false
        rotateStop = true
      })
      addTouchButton(game.width - radius - 32, buttonY, "Fire", () => {
        fire = true
      }, () => {
        fire = false
      })
      addTouchButton(game.width - radius * 3 - 64, buttonY, "Thrust", () => {
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

  def addMouseControls(bg: Sprite, player: Sprite): Unit = {
    bg.inputEnabled = true
    bg.events.onInputDown.add((spr: Sprite, ptr: Pointer) => {
      if (ptr.leftButton.isDown) fire = true
      if (ptr.rightButton.isDown) {
        val deltaX = player.x - ptr.x
        val deltaY = player.y - ptr.y
        val delta =  if (math.abs(deltaX) > math.abs(deltaY)) deltaX else deltaY
        if (math.abs(delta) > 10) joystickRotation = math.atan2(deltaY,deltaX)
      }
    }, null, 0)
    bg.events.onInputUp.add((spr: Sprite, ptr: Pointer) => {
      if (!ptr.leftButton.isDown) fire = false
      if (!ptr.rightButton.isDown) joystickRotation = JoystickReleased
    }, null, 0)
  }


  def addJoystick(): Unit = {
    val pos = new Point(256, game.height - 256)
    val cc = game.add.sprite(pos.x, pos.y, GlobalRes.ButtonId, 1)
    touchButtons.add(cc)
    cc.anchor.set(0.5, 0.5)
    cc.alpha = 0.25
    cc.scale.set(2.5,2.5)
    val c = game.add.sprite(pos.x, pos.y, GlobalRes.ButtonId)
    touchButtons.add(c)
    c.anchor.set(0.5, 0.5)
    c.alpha = 0.25
    c.scale.set(2.0, 2.0)
    c.inputEnabled = true
    c.input.enableDrag(lockCenter = false, bringToTop = false, pixelPerfect = false,
      boundsRect = new Rectangle(pos.x-256, pos.y-256, 512,512))
    c.events.onDragUpdate.add((c: Sprite, pointer: Pointer, x: Double, y: Double, snap: Point, fromStart: Boolean) => {
      val deltaX = pos.x - x
      val deltaY = pos.y - y
      val delta =  if (math.abs(deltaX) > math.abs(deltaY)) deltaX else deltaY
      if (math.abs(delta) > 10) joystickRotation = math.atan2(deltaY,deltaX)
      rotateLeft = false
      rotateRight = false
      // if (delta > 0) rotateLeft = true else if (delta < 0) rotateRight = true
    }, null, 1)
    c.events.onDragStop.add((c: Sprite, pointer: Pointer) => {
      c.x = pos.x
      c.y = pos.y
      joystickRotation = JoystickReleased
      rotateLeft = false
      rotateRight = false
    }, null, 1)
  }
}
