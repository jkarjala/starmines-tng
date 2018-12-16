package com.jpkware.smng

import com.definitelyscala.phaser.Physics.Arcade.Body
import com.definitelyscala.phaser._

case class ShipLevelInfo(bonusoidLimit: Int, bulletLifespan: Int, fireRate: Int, bulletSpeed: Int, shield: Double)

class Player(game: Game, x: Double, y: Double, bonusoidCount: Int)
  extends PreRotatedSprite(game, x,y, GlobalRes.MainAtlasId, Player.ShipPrefix, 64) {

  val ShipLevelInfos = Seq(
    ShipLevelInfo(0, 700, 300, 1000, 1.0),
    ShipLevelInfo(16, 800, 300, 1000, 1.0),
    ShipLevelInfo(32, 800, 250, 1000, 1.0),
    ShipLevelInfo(64, 900, 250, 1000, 1.0),
    ShipLevelInfo(100, 900, 200, 1000, 1.0),
    ShipLevelInfo(150, 1000, 200, 1000, 1.0),
    ShipLevelInfo(200, 1000, 150, 1000, 1.0),
    ShipLevelInfo(300, 1000, 125, 1000, 1.0),
    ShipLevelInfo(400, 1000, 100, 1000, 1.0),
  )
  val dualMissileBonusoidLimit: Int = 500

  game.physics.arcade.enable(this)
  physBody.drag.set(10,10)
  physBody.maxVelocity.set(750,750)
  physBody.collideWorldBounds = true
  physBody.bounce.set(1,1)

  val weapon1: Weapon = game.add.weapon(10, GlobalRes.MainAtlasId, Player.MissileId)
  weapon1.trackSprite(this, 0, 0, trackRotation = false)
  val weapon2: Weapon = game.add.weapon(10, GlobalRes.MainAtlasId, Player.MissileId)
  weapon2.trackSprite(this, 0, 0, trackRotation = false)
  var shipLevel: Int = 0
  resetWeapon(weapon1, ShipLevelInfos.head)
  maybeUpgradeShip(bonusoidCount)

  val FlameScalaMax: Double = fullWidth/80
  val flame: Sprite = game.add.sprite(x,y,Player.FlameId)
  flame.anchor = new Point(1.0, 0.5)
  flame.visible = false
  var flameScale = 0.25
  game.physics.arcade.enable(flame)

  val sfxThrust: Sound = game.add.audio(Player.SfxThrustId)
  sfxThrust.allowMultiple = false
  sfxThrust.volume = 0.4

  val sfxZap: Sound = game.add.audio(Player.SfxZapId)
  sfxZap.allowMultiple = true
  sfxZap.volume = 0.5

  var immortal: Boolean = true
  revive()

  def dualMissiles: Boolean = shipLevel >= ShipLevelInfos.length

  def maybeUpgradeShip(bonusoidCount: Int): Option[Int] = {
    val level = if (bonusoidCount<dualMissileBonusoidLimit) {
      ShipLevelInfos.count(_.bonusoidLimit <= bonusoidCount) - 1
    }
    else {
      // Drop the weapon level only partly when dual missiles enabled
      val count = bonusoidCount - dualMissileBonusoidLimit + 100
      ShipLevelInfos.length + ShipLevelInfos.count(_.bonusoidLimit <= count) - 1
    }
    if (level>shipLevel) {
      shipLevel = level
      if (!dualMissiles) {
        val levelInfo = ShipLevelInfos(level)
        Logger.info(s"$bonusoidCount => Single missiles $level $levelInfo")
        resetWeapon(weapon1, levelInfo)
        Some(level)
      }
      else {
        val newLevel = math.min(level - ShipLevelInfos.length, ShipLevelInfos.length-1)
        val levelInfo = ShipLevelInfos(newLevel)
        Logger.info(s"$bonusoidCount => Dual missiles $level $newLevel $levelInfo")
        resetWeapon(weapon1, levelInfo)
        resetWeapon(weapon2, levelInfo)
        if (newLevel<ShipLevelInfos.length) Some(level) else None
      }
    }
    else None
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

    game.physics.arcade.accelerationFromRotation(indexRotation, 500, physBody.acceleration)
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

    if (dualMissiles) {
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

  override def kill(): Sprite = {
    stop()
    flame.kill()
    super.kill()
  }

  override def revive(health: Double = 1): Sprite = {
    alpha = 0.5
    flame.alpha = 0.5
    immortal = true
    val timer = game.time.create(true)
    timer.add(1000, () => {
      immortal = false
      alpha = 1.0
      flame.alpha = 1.0
    }, null)
    timer.start(0)

    super.revive(health)
  }
}

object Player {
  def MissileId = "missile"
  def FlameId = "flame"
  def SfxZapId = "sfx:zap"
  def SfxThrustId = "sfx:thrust"
  def ShipPrefix = "ship"

  def preloadResources(game: Game): Unit = {
    game.load.image(FlameId, "res/flame.png")
    game.load.audio(SfxZapId, "res/zap.wav")
    game.load.audio(SfxThrustId, "res/thrust.wav")
  }
}