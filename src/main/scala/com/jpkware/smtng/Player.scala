/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.Physics.Arcade.Body
import com.definitelyscala.phaser._

case class ShipLevelInfo(bonusoidLimit: Int, bulletLifespan: Int, fireRate: Int, bulletSpeed: Int,
                         dual: Boolean, shipSpeed: Int, shipAccel: Int, msg: String)

class Player(game: Game, x: Double, y: Double, bonusoidCount: Int)
  extends PreRotatedSprite(game, x,y, GlobalRes.MainAtlasId, Player.ShipPrefix, 64) {

  private val ShipLevelInfos = Seq(
    ShipLevelInfo(0, 700, 300, 800, false, 450, 400, "Base level"),

    ShipLevelInfo(16, 750, 300, 800, false, 600, 400, "Missile range and ship speed upgraded"),
    ShipLevelInfo(32, 800, 300, 850, false, 700, 400, "Missile range and ship speed upgraded"),
    ShipLevelInfo(64, 800, 250, 900, false, 700, 500, "Missile fire rate and ship thrusters upgraded"),
    ShipLevelInfo(100, 800, 200, 900, false, 700, 500, "Missile fire rate upgraded"),
    ShipLevelInfo(150, 800, 200, 950, false, 750, 500, "Missile range and ship speed upgraded"),

    ShipLevelInfo(200, 800, 175, 950, false, 750, 500, "Missile fire rate upgraded"),
    ShipLevelInfo(300, 800, 150, 950, false, 750, 500, "Missile fire rate upgraded"),
    ShipLevelInfo(400, 800, 150, 1000, false, 750, 500, "Missile speed upgraded"),
    ShipLevelInfo(500, 800, 130, 1000, false, 750, 500, "Missile fire rate upgraded"),
    ShipLevelInfo(600, 850, 120, 1000, false, 750, 500, "Missile range and fire rate upgraded"),
    ShipLevelInfo(700, 850, 110, 1000, false, 750, 500, "Missile fire rate upgraded"),

    ShipLevelInfo(800, 800, 175, 950, true, 750, 500, "Dual missiles installed"),
    ShipLevelInfo(900, 800, 150, 950, true, 750, 500, "Dual missiles fire rate upgraded"),
    ShipLevelInfo(1000, 800, 150, 1000, true, 750, 500, "Dual missiles speed upgraded"),
    ShipLevelInfo(1100, 800, 130, 1000, true, 750, 500, "Dual missiles fire rate upgraded"),

    ShipLevelInfo(1200, 850, 120, 1000, true, 750, 500, "Dual missiles range and fire rate upgraded"),
    ShipLevelInfo(1300, 850, 110, 1000, true, 750, 500, "Dual missiles range and fire rate upgraded")
  )

  game.physics.arcade.enable(this)
  physBody.drag.set(10,10)
  physBody.collideWorldBounds = true
  physBody.bounce.set(1,1)

  // weapons are needed in StatePlay for collision checking
  val weapon1: Weapon = game.add.weapon(10, GlobalRes.MainAtlasId, Player.MissileId)
  weapon1.trackSprite(this, 0, 0, trackRotation = false)
  val weapon2: Weapon = game.add.weapon(10, GlobalRes.MainAtlasId, Player.MissileId)
  weapon2.trackSprite(this, 0, 0, trackRotation = false)
  resetWeapon(weapon1, ShipLevelInfos.head)

  private var shipLevelIndex: Int = 0

  private val FlameScalaMax: Double = fullWidth/80
  private val flame: Sprite = game.add.sprite(x,y,Player.FlameId)
  flame.anchor = new Point(1.0, 0.5)
  flame.visible = false
  private var flameScale = 0.25
  game.physics.arcade.enable(flame)

  private var shieldTime: Long = 0
  private val shield: Sprite = game.add.sprite(0,0,GlobalRes.ButtonId, PhaserButton.FrameButton)
  shield.anchor = new Point(0.5,0.5)
  this.addChild(shield)

  private val sfxThrust: Sound = game.add.audio(Player.SfxThrustId)
  sfxThrust.allowMultiple = false
  sfxThrust.volume = 0.4

  private val sfxZap: Sound = game.add.audio(Player.SfxZapId)
  sfxZap.allowMultiple = true
  sfxZap.volume = 0.5

  private val sfxUpgrade: Sound = game.add.audio(Player.SfxUpgradeId)
  sfxUpgrade.allowMultiple = true

  maybeUpgradeShip(bonusoidCount, sound=false)
  physBody.maxVelocity.set(ShipLevelInfos.head.shipSpeed, ShipLevelInfos.head.shipSpeed)

  revive()

  def shipLevel: Int = shipLevelIndex+1

  def shipLevelMsg(): String = {
    val next = if (shipLevelIndex<ShipLevelInfos.length-1) {
      s", next upgrade at ${ShipLevelInfos(shipLevelIndex+1).bonusoidLimit} B'soids"
    } else ""
    s"level $shipLevel$next"
  }

  def maybeUpgradeShip(bonusoidCount: Int, sound: Boolean = true): Option[String] = {
    val level = ShipLevelInfos.count(_.bonusoidLimit <= bonusoidCount) - 1
    val res = if (level>shipLevelIndex && level<ShipLevelInfos.length) {
      shipLevelIndex = level
      val levelInfo = ShipLevelInfos(level)
      physBody.maxVelocity.set(levelInfo.shipSpeed, levelInfo.shipSpeed)

      if (!levelInfo.dual) {
        Logger.info(s"$bonusoidCount => Single missiles $level $levelInfo")
        resetWeapon(weapon1, levelInfo)
        Some(levelInfo.msg)
      }
      else {
        Logger.info(s"$bonusoidCount => Dual missiles $level $levelInfo")
        resetWeapon(weapon1, levelInfo)
        resetWeapon(weapon2, levelInfo)
        Some(levelInfo.msg)
      }
    }
    else None
    if (sound && res.nonEmpty) sfxUpgrade.play()
    res
  }

  def resetWeapon(weapon: Weapon, shipLevel: ShipLevelInfo): Unit = {
    weapon.bulletKillType = Weapon.KILL_LIFESPAN
    weapon.bulletLifespan = shipLevel.bulletLifespan
    weapon.bulletCollideWorldBounds = true
    weapon.bulletRotateToVelocity = true
    weapon.bulletSpeed = shipLevel.bulletSpeed
    weapon.bulletInheritSpriteSpeed = true
    weapon.fireRate = shipLevel.fireRate
    weapon.autofire = false
    weapon.bullets.forEach(setBounce _, null, checkExists = false)
  }

  def RotSpeed: Double = scala.math.Pi*2

  def rotateLeft(): Unit = setRotationSpeed(rotationSpeed + RotSpeed/8)
  def rotateRight(): Unit = setRotationSpeed(rotationSpeed - RotSpeed/8)
  def rotateStop(): Unit = setRotationSpeed(0)

  def rotateToFire(atan2Angle: Double): Unit = {
    val joystickRotation = atan2Angle - math.Pi
    val playerRotation = indexRotation
    if (math.abs(joystickRotation-playerRotation) < rotationStep + 0.001 ) {
      rotateStop()
    }
    else {
      if ((joystickRotation < playerRotation && math.abs(playerRotation - joystickRotation) < math.Pi)
        || (joystickRotation > playerRotation && math.abs(playerRotation - joystickRotation) > math.Pi)) setRotationSpeed(RotSpeed)
      else setRotationSpeed(-RotSpeed)
    }
  }

  def rotateOrThrust(atan2Angle: Double): Unit = {
    val joystickRotation = atan2Angle - math.Pi
    val playerRotation = indexRotation
    // Logger.info(s"$joystickRotation $indexRotation ${playerRotation-joystickRotation}")
    if (math.abs(joystickRotation-playerRotation) < rotationStep + 0.001 ) {
      rotateStop()
      thrust()
    }
    else {
      thrustStop()
      if ((joystickRotation < playerRotation && math.abs(playerRotation - joystickRotation) < math.Pi)
        || (joystickRotation > playerRotation && math.abs(playerRotation - joystickRotation) > math.Pi)) setRotationSpeed(RotSpeed)
      else setRotationSpeed(-RotSpeed)
    }
  }

  def thrust(): Unit = {
    if (!this.visible) return

    game.physics.arcade.accelerationFromRotation(indexRotation, ShipLevelInfos(shipLevelIndex).shipAccel, physBody.acceleration)
    flame.revive(1)
    flame.rotation = indexRotation
    flame.position = headPoint(-fullWidth/6)
    flame.scale.set(flameScale, flameScale)
    if (flameScale<FlameScalaMax) flameScale += 0.1
    flame.body match {
      case body: Body =>
        body.acceleration = physBody.acceleration
        body.velocity = physBody.velocity
    }
    sfxThrust.play(forceRestart = false)
  }
  def thrustStop(): Unit = {
    physBody.acceleration.set(0,0)
    flame.kill()
    flameScale = 0.25
    sfxThrust.stop()
  }
  def stop(): Unit = {
    thrustStop()
    physBody.velocity.set(0,0)
  }

  def hide(): Unit = {
    stop()
    visible = false
  }

  def fire(): Unit = {
    if (!this.visible) return
    weapon1.fireAngle = indexAngle
    weapon2.fireAngle = indexAngle

    if (ShipLevelInfos(shipLevelIndex).dual) {
      val bullet1 = weapon1.fire(rotatedPoint(fullWidth/4, indexRotation + math.Pi/2))
      val bullet2 = weapon2.fire(rotatedPoint(fullWidth/4, indexRotation - math.Pi/2))
      if (bullet1!=null || bullet2!=null) sfxZap.play()
    }
    else {
      val bullet = weapon1.fire(headPoint(fullWidth/2))
      if (bullet!=null) sfxZap.play()
    }
  }

  def setBounce(bullet: Bullet): Unit = {
    bullet.body match { case body: Body => body.bounce.set(1,1) }
  }

  def shielded: Boolean = shieldTime > 0

  def addShield(time: Int): Unit = {
    alpha = 0.75
    flame.alpha = 0.75
    shield.alpha = 0.4
    shield.visible = true
    shieldTime += time
  }

  override def kill(): Sprite = {
    stop()
    flame.kill()
    super.kill()
  }

  override def revive(health: Double = 1): Sprite = {
    addShield(1000)
    super.revive(health)
  }

  override def update(): Unit = {
    super.update()
    if (shieldTime>0) {
      shieldTime -= game.time.physicsElapsedMS.toLong
      if (shieldTime<1000) shield.alpha = 0.4 * (shieldTime/1000.0)
      if (shieldTime<=0) {
        alpha = 1.0
        flame.alpha = 1.0
        shield.visible = false
      }
    }
  }
}

object Player {
  def MissileId = "missile"
  def FlameId = "flame"
  def SfxZapId = "sfx:zap"
  def SfxThrustId = "sfx:thrust"
  def SfxUpgradeId = "sfx:upgrade"
  def ShipPrefix = "ship"

  def preloadResources(game: Game): Unit = {
    game.load.image(FlameId, "res/flame.png")
    game.load.audio(SfxZapId, "res/zap.wav")
    game.load.audio(SfxThrustId, "res/thrust.wav")
    game.load.audio(SfxUpgradeId, "res/upgrade.wav")
  }
}