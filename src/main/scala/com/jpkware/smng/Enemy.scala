package com.jpkware.smng

import com.definitelyscala.phaser.Physics.Arcade.Body
import com.definitelyscala.phaser._

import scala.scalajs.js

class Enemy(game: Game, rule: Rule, group: Group) extends Sprite(game, 0,0, GlobalRes.EnemiesAtlasId, rule.shape+"01") {

  animations.add("rotate", Animation.generateFrameNames(rule.shape, 1, rule.frames, "", 2))
  val animation = animations.play("rotate", rule.fps, loop = true)
  anchor.set(0.5,0.5)
  group.add(this)
  // Logger.info(s"Added $frameName to ${group.name}")

  game.physics.arcade.enable(this)
  physBody.collideWorldBounds = true
  physBody.bounce.set(1,1)
  physBody.maxVelocity.set(500,500)

  def killScore: Int = rule.score

  def physBody: Body = body match {
    case body: Body => body
  }

  def bulletHit(bullet: Bullet): Int = {
    Explosion(game, Explosion.SmallExploCount).explode(this)
    bullet.kill()
    kill()
    killScore
  }

  override def reset(x: Double, y: Double, health: Double = 1): Sprite = {
    super.reset(x, y, health)
    physBody.velocity.set(rule.spd-StarMinesNG.rnd.nextDouble()*(2*rule.spd), rule.spd-StarMinesNG.rnd.nextDouble()*(2*rule.spd))
    this
  }

  override def update(): Unit = {
    val b = physBody
    val f = game.cache.getFrameData(GlobalRes.EnemiesAtlasId).getFrameByName(frameName)
    b.offset = new Point(f.spriteSourceSizeX, f.spriteSourceSizeY)
    b.width = f.width
    b.height = f.height
    super.update()
  }
}

object Enemy {
  def spawn(game: Game, rule: Rule, group: Group) : Enemy = new Enemy(game, rule, group)
}