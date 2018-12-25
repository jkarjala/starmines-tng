/*
 * This file is part of StarMines: The Next Generation - Copyright 2018-2019 Jari Karjala - www.jpkware.com
 * SPDX-License-Identifier: GPLv3-only
 */
package com.jpkware.smtng

import com.definitelyscala.phaser._

import scala.collection.mutable
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.Random

@JSExportTopLevel("ExplosionItem")
class ExplosionItem(game: Game, x: Double, y: Double, frame: String) extends Image(game, x,y, frame) {
  var vx: Double = _
  var vy: Double = _
  alive = false
  visible = false

  override def reset(x: Double, y: Double, health: Double = 1): Image = {
    super.reset(x, y, health)
    val spd : Double = Random.nextDouble() * 8
    val theta: Double = Random.nextDouble() * 2 * scala.math.Pi
    vx = spd * scala.math.cos(theta)
    vy = spd * scala.math.sin(theta)
    scale.set(0.5,0.5)
    this
  }
}

class Explosion(game: Game, count:Int) extends SpriteBatch(game, null,null, false) {

  var lifespan: Double = _

  classType = js.Dynamic.global.ExplosionItem
  createMultiple(count, Explosion.ExploId)

  game.add.existing(this)
  alive = false
  exists = false

  val sfxExplo: Sound = game.add.audio(Explosion.SfzExploId)
  sfxExplo.allowMultiple = true
  val sfxTinyexp: Sound = game.add.audio(Explosion.SfzTinyExploId)
  sfxTinyexp.allowMultiple = true

  def explode(sprite: Sprite, lifespan: Double = 10*count): Unit = {
    alive = true
    exists = true
    this.lifespan = lifespan
    val sx: Double = sprite.position.x
    val sy: Double = sprite.position.y
    children.foreach {
      case i: ExplosionItem =>
        i.reset(sx,sy)
        i.lifespan = lifespan
    }
    if (lifespan<1000) sfxTinyexp.play() else  sfxExplo.play()
  }

  override def update(): Unit = {
    if (!alive) return

    forEachAlive((child: ExplosionItem) => {
      child.x += child.vx
      child.vx *= 0.97
      child.y += child.vy
      child.vy *= 0.97
      val ratio = child.lifespan/lifespan
      child.alpha = ratio
      child.scale.set(0.4*ratio, 0.4*ratio)
    }, null)
    if (countLiving()==0) {
      alive = false
      exists = false
    }
  }
}

object Explosion {
  private val groupMap = mutable.Map[Int, Group]()
  private val maxExplosions = 10
  val TinyExploCount: Int = 20
  val SmallExploCount: Int = 50
  val LargeExploCount: Int = 200

  val ExploId = "explo"
  val SfzExploId = "sfx:explo"
  val SfzTinyExploId = "sfx:tinyexp"

  def apply(game: Game, count:Int, sprite: Sprite): Explosion = {
    val group = groupMap.getOrElse(count, sys.error(s"Explosion size $count was not initialized"))
    group.getFirstDead() match {
      case exp : Explosion =>
        exp.explode(sprite)
        exp
      case _ =>
        Logger.warn(s"No more explosions for size $count")
        null
    }
  }

  def initGroups(game: Game, counts: Seq[Int]): Unit = {
    counts.foreach(count => {
      val group = game.add.group()
      (1 to maxExplosions).foreach(i => group.add(new Explosion(game, count)))
      groupMap(count) = group
    })
  }

  def preloadResources(game: Game): Unit = {
    game.load.audio(SfzExploId, "res/explo.wav")
    game.load.audio(SfzTinyExploId, "res/tinyexp.wav")
    game.load.image(ExploId, "res/explo.png")
  }
}
