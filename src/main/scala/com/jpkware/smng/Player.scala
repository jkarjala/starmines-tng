package com.jpkware.smng

import com.definitelyscala.phaser.Physics.Arcade.Body
import com.definitelyscala.phaser.{Bullet, Game, Point, Weapon}

class Player(game: Game, x: Double, y: Double)
  extends PreRotatedSprite(game, x,y, "sprites", "ship", 64) {
  game.physics.arcade.enable(this)
  physBody.drag.set(10,10)
  physBody.maxVelocity.set(500,500)
  physBody.collideWorldBounds = true
  physBody.bounce.set(1,1)

  val weapon: Weapon = game.add.weapon(10, "missile")
  weapon.bulletKillType = Weapon.KILL_LIFESPAN
  weapon.bulletLifespan = 1000
  weapon.bulletCollideWorldBounds = true
  weapon.bulletRotateToVelocity = true
  weapon.bulletSpeed = 900
  weapon.bulletInheritSpriteSpeed = true
  weapon.fireRate = 300
  weapon.autofire = false
  weapon.bullets.forEach(setBounce _, null, false)
  weapon.trackSprite(this, 0,0, false)

  val FlameScalaMax = fullWidth/80
  val flame = game.add.sprite(x,y,"flame")
  flame.anchor = new Point(1.0, 0.5)
  flame.visible = false
  var flameScale = 0.25
  game.physics.arcade.enable(flame)

  Logger.info("Player constructed")

  def rotateLeft(): Unit = setRotationSpeed(scala.math.Pi*2)
  def rotateRight(): Unit = setRotationSpeed(-scala.math.Pi*2)
  def rotateStop(): Unit = setRotationSpeed(0)

  def thrust(): Unit = {
    game.physics.arcade.accelerationFromRotation(indexRotation, 250, physBody.acceleration)
    flame.rotation = indexRotation
    flame.position = headPoint(-fullWidth/6)
    flame.scale.set(flameScale, flameScale)
    if (flameScale<FlameScalaMax) flameScale += 0.1
    flame.body match {
      case body: Body =>
        body.acceleration = physBody.acceleration
        body.velocity = physBody.velocity
    }
    flame.visible = true
  }
  def brake(): Unit = {
    physBody.acceleration.set(0,0)
    flame.visible = false
    flameScale = 0.25
  }
  def stop(): Unit = {
    brake()
    physBody.velocity.set(0,0)
  }

  def fire(): Bullet = {
    weapon.fireAngle = indexAngle
    weapon.fire(headPoint(fullWidth/2))
  }

  def setBounce(bullet: Bullet): Unit = {
    bullet.body match { case body: Body => body.bounce.set(1,1) }
  }
}
