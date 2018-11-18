package com.jpkware.smng

import com.definitelyscala.phaser.Physics.Arcade.Body
import com.definitelyscala.phaser.{Game, Point, Sprite}

class PreRotatedSprite(game: Game, x: Double, y: Double, sheet: String, prefix: String, count: Int)
  extends Sprite(game, x,y, sheet, prefix+"01") {

  val Pi2: Double = 2*scala.math.Pi
  protected val fullWidth: Double = width

  protected var direction = 0.0
  protected var index = 1
  protected var rotationSpeed = 0.0

  anchor = new Point(0.5,0.5)

  def physBody: Body = body match {
    case body: Body => body
  }

  protected def selectFrame(): Unit = {
    val idx = ((direction/Pi2) * count).toInt + 1
    if (idx!=index) {
      frameName = f"$prefix$idx%02d"
      val f = game.cache.getFrameData("sprites").getFrameByName(frameName)
      val b = physBody
      b.offset = new Point(f.spriteSourceSizeX, f.spriteSourceSizeY)
      b.width = f.width
      b.height = f.height
      index = idx
    }
  }

  protected def setRotationSpeed(s: Double): Unit = {
    if (s >= -scala.math.Pi*3 && s <= scala.math.Pi*3)
        rotationSpeed = s
  }

  val rotationStep: Double = Pi2/count
  def indexRotation: Double = -(index-1) * rotationStep

  def indexAngle: Double = scala.math.toDegrees(indexRotation)

  def headPoint(radius: Double): Point = {
    val rot = indexRotation
    val pt = new Point(centerX + scala.math.cos(rot)*radius, centerY + scala.math.sin(rot)*radius)
    pt
  }
  override def update(): Unit = {
    super.update()
    direction += rotationSpeed * game.time.physicsElapsed
    if (direction<0) direction = Pi2 - 0.001
    if (direction>2*scala.math.Pi) direction = 0
    selectFrame()
  }
}

