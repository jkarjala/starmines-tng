/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2020 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser._

class XYShooter(game: Game, rule: Rule, group: Group, enemyMissiles: Group, player: Player) extends Enemy(game, rule, group) {
  private val xMul: Int = rule.args(0).toInt
  private val yMul: Int = rule.args(1).toInt
  private val missileSpeed: Int = rule.args(2).toInt

  private val velo = new Point(xMul, yMul)
  private var lastFrame = ""

  private val sfxZap: Sound = game.add.audio(Player.SfxZapId)
  sfxZap.allowMultiple = true

  val missileRule: Rule = Rule(null, Player.MissileId, 1, 1, 0, spd = missileSpeed)
  val missiles: Group = game.add.group()
  (1 to 1).foreach(i => {
    val b = new EnemyMissile(game, missileRule, missiles, velo, player)
    b.tint = 0x00FF00
    b.kill()
  })

  override def bulletHit(bullet: Sprite): Int = {
    if (frameName.contains("01") || frameName.contains("02") || frameName.contains("03")
      || frameName.contains(s"${rule.frames}") || frameName.contains(s"${rule.frames-1}"))
      damage(1.0)
    else
      damage(0.2)

    if (health>0) {
      Explosion(game, Explosion.TinyExploCount, this)
      animation.speed *= 0.5
      bullet.kill()
      0
    } else {
      super.bulletHit(bullet)
    }
  }

  override def reset(x: Double, y: Double, health: Double): Sprite = {
    super.reset(x, y, health)
    physBody.velocity.set(xMul * physBody.velocity.x, yMul * physBody.velocity.y)
    this
  }

  override def update(): Unit = {
    if (frameName.contains("01") && lastFrame!=frameName) {
      missiles.getFirstDead() match {
        case b: EnemyMissile =>
          b.reset(position.x, position.y, 1)
          sfxZap.play(volume = 0.2)
          enemyMissiles.add(b)
        case _ => Logger.warn("No more EnemyMissiles")
      }
    }
    lastFrame = frameName
    super.update()
  }
}

class EnemyMissile (game: Game, rule: Rule, missiles: Group, velo: Point, player: Player) extends Enemy(game, rule, missiles) {
  val Lifespan = 1500

  override def reset(x: Double, y: Double, health: Double): Sprite = {
    lifespan = Lifespan
    val xs = math.signum(player.x - x)
    val ys = math.signum(player.y - y)
    rotation = math.atan2(velo.y, velo.x)
    super.reset(x + velo.x * width/2 * xs, y + velo.y * height/2 * ys, health)
    physBody.velocity.set(xs * velo.x * rule.spd, ys * velo.y * rule.spd)
    this
  }

  override def kill(): Sprite = {
    missiles.add(this)
    super.kill()
  }

  override def enemyHit(theOther: Enemy): Boolean = {
    theOther match {
      case xy: XYShooter =>
        if (lifespan < Lifespan-200)
          kill() // hitting XYShooter simply kills the bullet if the lifespan has decreased
      case em: EnemyMissile =>
        // Do not kill other enemy missiles
      case other: Enemy =>
        other.bulletHit(this)
    }
    true
  }
}

object XYShooter {
  def spawn(p: SpawnParams) : Enemy = new XYShooter(p.game, p.rule, p.group, p.enemyMissiles, p.player)
}