package com.jpkware.smng

import com.definitelyscala.phaser._

class PathWorm(game: Game, rule: Rule, group: Group) extends PathMine(game, rule, group) {
  val count = rule.args(1).toInt

  override def reset(x: Double, y: Double, health: Double = 1): Sprite = {
    super.reset(x,y,health)
    (1 to count).foreach(i => {
      val tailRule = rule.copy(args = Seq(s"i"*(i*rule.spd/10) + rule.args(0)))
      val b = new PathMine(game, tailRule, group)
      b.reset(x,y,health)
      b.physBody.velocity.set(physBody.velocity.x, physBody.velocity.y)
    })
    this
  }
}

object PathWorm {
  def spawn(game: Game, rule: Rule, group: Group) : Enemy = new PathWorm(game, rule, group)
}