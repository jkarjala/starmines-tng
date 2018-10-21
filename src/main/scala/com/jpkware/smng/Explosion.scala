package com.jpkware.smng

import com.definitelyscala.phaser._

import scala.collection.mutable
import scala.util.Random

class ExplosionItem(game: Game, x: Double, y: Double) extends Image(game, x,y, "explo") {
  var vx: Double = _
  var vy: Double = _
  alive = false
  visible = false

  override def reset(x: Double, y: Double, health: Double = 1): Image = {
    super.reset(x, y, health)
    val spd : Double = Random.nextDouble() * 5
    val theta: Double = Random.nextDouble() * 2 * scala.math.Pi
    vx = spd * scala.math.cos(theta)
    vy = spd * scala.math.sin(theta)
    scale.set(0.25,0.25)
    this
  }
}

class Explosion(game: Game, count:Int) extends SpriteBatch(game, null,null, false) {

  var lifespan: Double = _

  (1 to count).foreach( i => {
    val item = new ExplosionItem(game, 0,0)
    this.add(item)
  })
  game.add.existing(this)
  alive = false
  exists = false

  def explode(sprite: Sprite, lifespan: Double): Unit = {
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
      child.scale.set(0.25*ratio, 0.25*ratio)
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
  val SmallExploCount: Int = 50
  val LargeExploCount: Int = 200

  def apply(game: Game, count:Int): Explosion = {
    val group = groupMap.getOrElse(count, sys.error(s"Explosion size $count was not initialized"))
    group.getFirstDead() match {
      case exp : Explosion => exp
      case _ => null
    }
  }

  def initGroups(game: Game, counts: Seq[Int]): Unit = {
    counts.foreach(count => {
      val group = game.add.group()
      (1 to maxExplosions).foreach(i => group.add(new Explosion(game, count)))
      groupMap(count) = group
    })
  }
}