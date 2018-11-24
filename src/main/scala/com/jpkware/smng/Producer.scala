package com.jpkware.smng

import com.definitelyscala.phaser.Physics.Arcade.Body
import com.definitelyscala.phaser._

class Producer(game: Game, rule: Rule, group: Group) extends Enemy(game, rule, group) {
  val pieces: Group = game.add.group()
  val shape: String = rule.args(0)
  val frames: Int = rule.args(1).toInt
  val fps: Int = rule.args(2).toInt
  val count: Int = rule.args(3).toInt
  val delay: Int = rule.args(4).toInt
  val score: Int = rule.score/count
  val pieceRule: Rule = Rule(null, shape, frames, fps, score, spd = rule.spd)

  val sfxProduce: Sound = game.add.audio(Producer.SfzProduceId)
  sfxProduce.allowMultiple = true

  (1 to count * 4).foreach(i => {
    val b = new ProducerPiece(game, pieceRule, pieces)
    b.kill()
  })

  val timer = game.time.create(true)
  timer.loop(delay, spawnPieces _, null)
  timer.start(100)

  override def bulletHit(bullet: Sprite): Int = {
    damage(0.25)
    if (health>0) {
      Explosion(game, Explosion.TinyExploCount).explode(this)
      animation.speed *= 0.5
      bullet.kill()
      0
    } else {
      timer.destroy()
      super.bulletHit(bullet)
    }
  }

  override def reset(x: Double, y: Double, health: Double): Sprite = {
    super.reset(x, y, health)
    physBody.velocity.set(0,0)
    this
  }

  def spawnPieces(): Unit = {
    var spawned = false
    // pieces.forEach((c: Sprite) => { Logger.info(s"Producer timer: ${c.frameName} ${c.alive} ${c.exists}  ${c.centerX} ${c.centerX}")}, null, false)

    (1 to count).foreach(i => {
      val b = pieces.getFirstDead()
      b match {
        case b: ProducerPiece =>
          b.reset(position.x, position.y, 1)
          group.add(b)
          spawned = true
          b.body match {
            case body: Body =>
              val v = rule.spd * animation.speed
              val vel = new Point(v * StarMinesNG.rnd.nextGaussian(), v * StarMinesNG.rnd.nextGaussian())
              body.velocity = vel
          }
        case _ => // No more pieces to resurrect, must wait until player shoots them
      }
    })
    if (spawned) sfxProduce.play()
  }
}

class ProducerPiece(game: Game, rule: Rule, pieces: Group) extends Enemy(game, rule, pieces) {
  override def bulletHit(bullet: Sprite): Int = {
    pieces.add(this)
    super.bulletHit(bullet)
  }
}

object Producer {
  val SfzProduceId = "sfx:produce"

  def spawn(p: SpawnParams) : Enemy = new Producer(p.game, p.rule, p.group)

  def preloadResources(game: Game): Unit = {
    game.load.audio(SfzProduceId, "res/produce.wav")
  }

}