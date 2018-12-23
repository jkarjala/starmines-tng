package com.jpkware.smng

import com.definitelyscala.phaser._
import org.scalajs.dom

import scala.scalajs.js

class ButtonPositions extends js.Object{
  var rightX: Double = 0.0
  var rightY: Double = 0.0
  var leftX: Double = 0.0
  var leftY: Double = 0.0
  var thrustX: Double = 0.0
  var thrustY: Double = 0.0
  var fireX: Double = 0.0
  var fireY: Double = 0.0
}

object ButtonPositions {
  val radius = 128
  val margin = 24
  def apply(game: Game): Seq[ButtonPositions] = {
    val buttonY: Double = game.height - radius - margin
    val positions = Seq(
      new ButtonPositions {
        rightX = radius + margin
        rightY = buttonY
        leftX = radius * 3 + margin*2
        leftY = buttonY
        thrustX = game.width - radius * 3 - margin*2
        thrustY = buttonY
        fireX = game.width - radius - margin
        fireY = buttonY
      },
      new ButtonPositions {
        rightX = radius + margin
        rightY = game.height/2 - radius - margin
        leftX = radius + margin
        leftY = game.height/2 + radius + margin
        fireX = game.width - radius - margin
        fireY = game.height/2 - radius - margin
        thrustX = game.width - radius - margin
        thrustY = game.height/2 + radius + margin
      },
      new ButtonPositions {
        thrustX = radius + margin
        thrustY = game.height/2 - radius - margin
        rightX = radius + margin
        rightY = game.height/2 + radius + margin
        fireX = game.width - radius - margin
        fireY = game.height/2 - radius - margin
        leftX = game.width - radius - margin
        leftY = game.height/2 + radius + margin
      },
      new ButtonPositions {
        thrustX = radius + margin
        thrustY = game.height/2 + radius + margin
        rightX = radius + margin
        rightY = game.height/2 - radius - margin
        fireX = game.width - radius - margin
        fireY = game.height/2 + radius + margin
        leftX = game.width - radius - margin
        leftY = game.height/2 - radius - margin
      }
    )
    positions
  }
  var current: ButtonPositions = _
}

class TouchControls(game: Game) {
  val touchButtons: Group = game.add.group()
  touchButtons.visible = false
  var stick: Boolean = false

  var rotateRight = false
  var rotateLeft = false
  var rotateStop = false
  var thrust = false
  var fire = false

  val LSControlIndex = "starmines-control-index"
  val NoRotation = 100.0  // Indicating "no action"
  var thrustRotation: Double = NoRotation
  var fireRotation: Double = NoRotation
  def isEnabled: Boolean = touchButtons.visible

  var currentLayout: Int = dom.window.localStorage.getItem(LSControlIndex) match {
    case item: String =>
      item.toInt
    case _ =>
      0
  }
  val positions: Seq[ButtonPositions] = ButtonPositions(game)
  if (currentLayout<0) stick = true else {
    ButtonPositions.current = positions(currentLayout)
  }

  def nextLayout(): Unit = {
    currentLayout += 1
    if (currentLayout==positions.length) {
      currentLayout = -1
      stick = true
    }
    else {
      ButtonPositions.current = positions(currentLayout)
      stick = false
    }
    touchButtons.destroy(destroyChildren = true, soft = true)
    addTouchButtons()
    enable()
    dom.window.localStorage.setItem(LSControlIndex, currentLayout.toString)
  }

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
    if (!stick) {
      addTouchButton(ButtonPositions.current.rightX,ButtonPositions.current.rightY, "", () => {
        rotateLeft = true
      }, () => {
        rotateLeft = false
        rotateStop = true
      }, textFrame=PhaserButton.FrameRotLeft)
      addTouchButton(ButtonPositions.current.leftX, ButtonPositions.current.leftY, "", () => {
        rotateRight = true
      }, () => {
        rotateRight = false
        rotateStop = true
      }, textFrame=PhaserButton.FrameRotRight)
      addTouchButton(ButtonPositions.current.thrustX, ButtonPositions.current.thrustY, "", () => {
        thrust = true
      }, () => {
        thrust = false
      }, textFrame=PhaserButton.FrameThrust)
      addTouchButton(ButtonPositions.current.fireX, ButtonPositions.current.fireY, "", () => {
        fire = true
      }, () => {
        fire = false
      }, textFrame=PhaserButton.FrameFire)
    }
    else {
      addTouchButton(game.width - 256, game.height-256, "", () => {
        fire = true
      }, () => {
        fire = false
      }, textFrame=PhaserButton.FrameFire)
    }

  }

  def addTouchButton(x: Double, y: Double, text: String, down: () => Unit, up: () => Unit, textFrame: Int = 0): Button = {
    val button = PhaserButton.add(game, x, y, text, 0.25, touchButtons, textFrame=textFrame, scale = 1.75 )
    button.events.onInputOver.add(down, null, 1)
    button.events.onInputOut.add(up, null, 1)
    button.events.onInputDown.add(down, null, 1)
    button.events.onInputUp.add(up, null, 1)
    button
  }

  def addMouseControls(bg: Sprite, player: Sprite): Unit = {
    bg.inputEnabled = true
    bg.events.onInputDown.add((spr: Sprite, ptr: Pointer) => {
      val deltaX = player.x - ptr.x
      val deltaY = player.y - ptr.y
      val delta =  if (math.abs(deltaX) > math.abs(deltaY)) deltaX else deltaY
      if (ptr.leftButton.isDown) {
        fire = true
        if (math.abs(delta) > 10) fireRotation = math.atan2(deltaY,deltaX)
      }
      if (ptr.rightButton.isDown) {
        if (math.abs(delta) > 10) thrustRotation = math.atan2(deltaY,deltaX)
      }
    }, null, 0)
    bg.events.onInputUp.add((spr: Sprite, ptr: Pointer) => {
      if (ptr.leftButton.isUp) {
        fireRotation = NoRotation
        fire = false
      }
      if (ptr.rightButton.isUp) {
        thrustRotation = NoRotation
        thrust = false
      }
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
      c.alpha = 1.0
      val deltaX = pos.x - x
      val deltaY = pos.y - y
      val delta =  if (math.abs(deltaX) > math.abs(deltaY)) deltaX else deltaY
      if (math.abs(delta) > 10) thrustRotation = math.atan2(deltaY,deltaX)
      rotateLeft = false
      rotateRight = false
      // if (delta > 0) rotateLeft = true else if (delta < 0) rotateRight = true
    }, null, 1)
    c.events.onDragStop.add((c: Sprite, pointer: Pointer) => {
      c.alpha = 0.25
      c.x = pos.x
      c.y = pos.y
      thrustRotation = NoRotation
      rotateLeft = false
      rotateRight = false
    }, null, 1)
  }
}
