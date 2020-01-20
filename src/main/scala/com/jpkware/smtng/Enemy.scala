/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2020 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser.Physics.Arcade.Body
import com.definitelyscala.phaser._

import scala.util.Random

class Enemy(game: Game, rule: Rule, group: Group, atlas: String = GlobalRes.EnemiesAtlasId, initialFrame: Int = 1)
  extends Sprite(game, 0,0, atlas, f"${rule.shape}%s$initialFrame%02d") {

  animations.add("rotate", Animation.generateFrameNames(rule.shape, 1, rule.frames, "", 2))
  val animation: Animation = animations.play("rotate", rule.fps, loop = true)

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

  def bulletHit(bullet: Sprite): Int = {
    Explosion(game, Explosion.SmallExploCount, this)
    bullet.kill()
    kill()
    killScore
  }

  def enemyHit(theOther: Enemy): Boolean = {
    false // try enemyHit the other way
  }

  override def reset(x: Double, y: Double, health: Double = 1): Sprite = {
    super.reset(x, y, health)
    animation.setFrame(rule.shape + s"${Random.nextInt(rule.frames)}")
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
  def spawn(p: SpawnParams) : Enemy = new Enemy(p.game, p.rule, p.group)
}